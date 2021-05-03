package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {

	Graph<Fermata, DefaultEdge> grafo;
	
	public void creaGrafo() {
		this.grafo=new SimpleGraph<>(DefaultEdge.class); 
		
		MetroDAO dao= new MetroDAO();
		List<Fermata> fermate=dao.getAllFermate();
		
		
//		for(Fermata f: fermate) {
//			this.grafo.addVertex(f);
//		}
		
		Graphs.addAllVertices(this.grafo, fermate);
		
		//Aggiungo archi
		//Metodo con doppio loop è poco pratico quando ho tanti vertici
		//è ok se ho un grafo piccolo
		/*
		for(Fermata f1:this.grafo.vertexSet()) {
			for(Fermata f2: this.grafo.vertexSet()) {
				//Tra queste due stazioni devo mettere arco?
				//Se se diversi, posso fare query a Dao chiedendo se sono collegate
				//facendo query ripetute ci metterò una vita ad eseguire
				//ne stiamo provando troppe
				if(!f1.equals(f2) && dao.fermateCollegate(f1, f2)) {
					this.grafo.addEdge(f1, f2);
				}
			}
		}*/
		
		List<Connessione> connessioni=dao.getAllConnessioni(fermate);
		for(Connessione c:connessioni)
			this.grafo.addEdge(c.getStazP(), c.getStazA());
		
		//System.out.println(this.grafo);
		
		/*Fermata f;
		Set<DefaultEdge> archi =this.grafo.edgesOf(f);
		for(DefaultEdge e:archi) {*/
			/*
			Fermata f1=this.grafo.getEdgeSource(e);
			//oppure
			Fermata f2=this.grafo.getEdgeTarget(e);
			if(f1.equals(f)) {
				//f2 è quello che mi serve
			} else {
				//f1 è quello che mi serve
			}*/
			
			//f1=Graphs.getOppositeVertex(this.grafo, e, f); //Prende grafo, arco, e vertice
		//}
		
		//List<Fermata> fermateAdiacenti =Graphs.successorListOf(this.grafo, f);
		//Graphs.predecessorListOf(null, null);
		//Nel grafo non orientato i due metodi sono equivalenti 
		//Archi entranti o uscenti non cambia nulla
	}
	
	public List<Fermata> fermateRaggiungibili(Fermata partenza){
		//BreadthFirstIterator<Fermata, DefaultEdge> bfv=new BreadthFirstIterator<>(this.grafo,partenza);
		
		DepthFirstIterator<Fermata, DefaultEdge> dfv=new DepthFirstIterator<>(this.grafo, partenza);
		
		List<Fermata> result=new ArrayList<>();
		
		while(dfv.hasNext()) {
			Fermata f=dfv.next();
			result.add(f);
		}
		
		return result;
	}
	
	
	public Fermata trovaFermata(String nome) {
		//Se questo metodo fosse chiamato molte volte potrei pensare di ottimizzarlo usando una Map
		for(Fermata f:this.grafo.vertexSet()) {
			if(f.getNome().equals(nome)) {
				return f;
			}
		}
		return null;
	}
}
