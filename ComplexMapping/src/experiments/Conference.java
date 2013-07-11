package experiments;

import complexMapping.ComplexMapping;
import complexMapping.ComplexMappingException;

/**
 * 
 * @author Dominique Ritze
 * 
 * Class to test the conference ontologies (cmt, confOf, ekaw, iasted, sigkdd).
 *
 */
public class Conference {
	
	public static void main(String[] args) throws ComplexMappingException {
				
		String ontFolder = "exp/ontologies/";
		String mappingFolder = "exp/reference/";
		String ontIds[] = {"cmt", "confOf", "ekaw", "iasted", "sigkdd"};			
		
		for (int i = 0; i < ontIds.length; i++) {			
			for (int j = i+1; j < ontIds.length; j++) {
				ComplexMapping cm = new ComplexMapping(ontFolder + ontIds[i]+".owl", ontFolder + ontIds[j]+".owl",mappingFolder + ontIds[i] + "-" + ontIds[j] + ".rdf" );
				cm.createAlignment();
				cm.writeAlignment("exp/complexAlignmentsConference/" + ontIds[i] + " - " + ontIds[j]+ ".txt");
			}
		}
	}

}
