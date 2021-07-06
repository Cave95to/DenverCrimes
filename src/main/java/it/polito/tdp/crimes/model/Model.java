package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	private SimpleWeightedGraph<String,DefaultWeightedEdge> grafo;
	private EventsDao dao;
	
	// ricorsione cammino piu lungo
	private List<String> percorsoMigliore;
	
	public Model() {
		dao = new EventsDao();
	}
	
	public void creaGrafo(String categoria, int mese) {
		
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		// aggiunta vertici
		
		Graphs.addAllVertices(this.grafo, this.dao.getVertici(categoria, mese));
		
		// aggiunta archi
		
		for (Adiacenza a : this.dao.getAdiacenze(categoria, mese)) {
			
			if(this.grafo.containsVertex(a.getT1()) && this.grafo.containsVertex(a.getT2())) {
				
				DefaultWeightedEdge e = this.grafo.getEdge(a.getT1(), a.getT2());
				
				if( e == null) { // se arco non esiste
					
					Graphs.addEdgeWithVertices(this.grafo, a.getT1(), a.getT2(), a.getPeso());
				}
			}
		}
		
		//System.out.println("vertici : "+ this.grafo.vertexSet().size()+ " archi: "+this.grafo.edgeSet().size());
	}

	public List<String> getCategorie() {
		return this.dao.getCategorie();
	}

	public List<Integer> getMesi() {
		return this.dao.getMesi();
	}

	public int getNVertici() {
		if(this.grafo!= null)
			return this.grafo.vertexSet().size();
		
		return 0;
	}

	public int getNArchi() {
		if(this.grafo!=null)
			return this.grafo.edgeSet().size();
		return 0;
	}
	
	public double calcolaMedia() {
		double media = 0.0;
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			media = media + this.grafo.getEdgeWeight(e);
		}
		return (media/this.getNArchi());
	}
	
	public List<Adiacenza> getArchiFiltrati() {
		if(this.grafo!= null) {
			double media = this.calcolaMedia();
			List<Adiacenza> result = new ArrayList<>();
			for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
				if(this.grafo.getEdgeWeight(e)>media)
					result.add(new Adiacenza(this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e), this.grafo.getEdgeWeight(e)));
			}
			
			return result;
		}
		return null;
		
	}
	
	// devo trovare percorso piu lungo USO RICORSIONE
	public List<String> trovaPercorso(String sorgente, String destinazione) {
		
		this.percorsoMigliore = new ArrayList<>();
		
		List<String> parziale = new ArrayList<>();
		parziale.add(sorgente);
		
		this.cerca(destinazione, parziale);
		
		return this.percorsoMigliore;
		
	}
	
	private void cerca(String destinazione, List<String> parziale) {
		
		// caso terminale
		if(parziale.get(parziale.size()-1).equals(destinazione)) {
			
			if(parziale.size()>this.percorsoMigliore.size())
				// FONDAMENTALE RICORDARSI DI FARE LA NEW
				this.percorsoMigliore = new ArrayList<>(parziale);
			
			return;
		}
		
		// altrimenti scorro i vicini dell'ultimo inserito e provo 1 a 1 ad aggiungere
		
		for (String vicino : Graphs.neighborListOf(this.grafo, parziale.get(parziale.size()-1))) {
			
			if(!parziale.contains(vicino)) { // DEVO EVITARE CICLI!!!
				parziale.add(vicino);
				this.cerca(destinazione, parziale);
				parziale.remove(parziale.size()-1);
			}
		}
	}
	
}
