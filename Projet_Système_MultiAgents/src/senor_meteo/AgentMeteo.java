package senor_meteo;


import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;


public class AgentMeteo extends Agent{

	Tabmeteo[] tab= new Tabmeteo[7];
	
	protected void setup(){
		
		ParallelBehaviour meteoparallele = new ParallelBehaviour(ParallelBehaviour.WHEN_ALL);
		
		
		
		meteoparallele.addSubBehaviour(new OneShotBehaviour(this){

			
			public void action(){
				System.out.println(getLocalName()+" lanc�");
				try {
				
					JSONObject json = senor_meteo.JsonReader.meteoFromUrl();// importation de la m�t�o depuis la fonction du package
					JSONArray listejour = json.getJSONArray("list"); // on prends la liste des pr�visions journali�re
					long valdat =0; 								// initialisation des variable et du tableau ou on les rentre
					String meteo;
					int temperature;
					
									// entr�e dans le tableau
					for (int i = 0; i < tab.length; i++) {
						JSONObject temp1 = listejour.getJSONObject(i);
						temperature = temp1.getJSONObject("temp").getInt("day");
						valdat=temp1.getLong("dt");
						meteo = temp1.getJSONArray("weather").getJSONObject(0).getString("description");

						tab[i] = new Tabmeteo();			// entr�e des donn�es dans le tableau
						tab[i].setDate(valdat);
						tab[i].setTemperature(temperature);
						tab[i].setMeteo(meteo);
					//	System.out.println(tab[i].toString());
						}
					  
//					  valtest=10;
				} catch (IOException | JSONException e) {
					// TODO Auto-generated catch block
					System.out.println("erreur: Senor meteo n'a pas re�u de donn�e du web.");
					e.printStackTrace();
				}
		  }
			
	  });
			
		meteoparallele.addSubBehaviour(new TickerBehaviour(this,43200000){// 43200000 = 12 heures en milisecondes | � modifier selon le rafraichissement voulu
				protected void onTick() {
					try {
						
						JSONObject json = senor_meteo.JsonReader.meteoFromUrl();// importation de la m�t�o depuis la fonction du package
						JSONArray listejour = json.getJSONArray("list"); // on prends la liste des pr�visions journali�re
						long valdat =0; 								// initialisation des variable et du tableau ou on les rentre
						String meteo;
						int temperature;
						
										// entr�e dans le tableau
						for (int i = 0; i < tab.length; i++) {
							JSONObject temp1 = listejour.getJSONObject(i);
							temperature = temp1.getJSONObject("temp").getInt("day");
							valdat=temp1.getLong("dt");
							meteo = temp1.getJSONArray("weather").getJSONObject(0).getString("description");

							tab[i] = new Tabmeteo();			// entr�e des donn�es dans le tableau
							tab[i].setDate(valdat);
							tab[i].setTemperature(temperature);
							tab[i].setMeteo(meteo);
						//	System.out.println(tab[i].toString());
							}
						  

					} catch (IOException | JSONException e) {
						// TODO Auto-generated catch block
						System.out.println("erreur: Senor meteo n'a pas re�u de donn�e du web.");
						e.printStackTrace();
					}
				}
			});
			
			meteoparallele.addSubBehaviour(new CyclicBehaviour(this) {
				public void action()
				{
				ACLMessage msg = receive();
				if(msg!= null)  {
					String nom = msg.getSender().getLocalName();
					String reponse ="";
					for (int i = 0; i < tab.length; i++) {
						reponse = reponse+tab[i].toString()+";";
						}
					envoimessage(nom,reponse);
					}
				
				else{
					block();
							};
				//TODO  g�rer la reception de message et l'envoi de message pour donner les pr�vision m�t�o 
				}
			});
			
			addBehaviour(meteoparallele);
			// ajout du comportement d�crit au dessus.
	}
	
	
	public void envoimessageAID (AID A,String contenu){
		ACLMessage message = new ACLMessage(ACLMessage.INFORM);
		message.setContent(contenu);
		message.addReceiver(A);
		send(message);
	}
	
	public void envoimessage (String destinataire,String contenu){
		ACLMessage message = new ACLMessage(ACLMessage.INFORM);
		message.setContent(contenu);
		message.addReceiver(new AID(destinataire, AID.ISLOCALNAME));
		send(message);
	}
	
	public void defibrillateur(String agentmort){
		
		ContainerController cc = getContainerController();
		
		switch (agentmort) {
		
        case "senor_meteo":
        	
        	try {
			AgentController ac = cc.createNewAgent("senor_meteo","senor_meteo.AgentMeteo", null);
			ac.start();} 
        	catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
        	};
                 break;
                 
        case "r2d2":
        	
        	try {
			AgentController ac = cc.createNewAgent("r2d2","r2d2.AgentEquipement", null);
			ac.start();}
        	catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
        	};
                 break;
                 
        case "c3po":  
        	
        	try {
			AgentController ac = cc.createNewAgent("c3po","c3po.AgentOccupant", null);
			ac.start();} 
        	catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
        	};
                 break;
                 
        case "glados": 
        	try {
			AgentController ac = cc.createNewAgent("senor_meteo","senor_meteo.AgentMeteo", null);
			ac.start();} 
        	catch (StaleProxyException e) {
			// TODO Auto-generated catch block
        		e.printStackTrace();
        	};
        		break; 
        		
        default: System.out.println("Erreur dans le reboot d'un agent par defibrillateur.") ;
                 break;}
		
	}
}


