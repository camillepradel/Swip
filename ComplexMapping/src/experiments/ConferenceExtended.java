package experiments;

import complexMapping.ComplexMapping;
import complexMapping.ComplexMappingException;

/**
 * 
 * @author Dominique Ritze
 * 
 * Class to print the complex correspondences of the extended conference ontologies
 * (cmt, sofsem, confOf, crs, ekaw, openconf, pcs, sigkdd)
 *
 */
public class ConferenceExtended {
	
	public static void main(String[] args) throws ComplexMappingException {
			
		String ontFolder = "exp/ontologiesExpanded/";
		String mappingFolder = "exp/referenceExpanded/";
		String ontIds[] = {"cmt", "sofsem", "confOf", "crs", "ekaw", "openconf", "pcs", "sigkdd"};	
		for (int i = 0; i < ontIds.length; i++) {			
			for (int j = i+1; j < ontIds.length; j++) {
				ComplexMapping cm = new ComplexMapping(ontFolder + ontIds[i]+".owl", ontFolder + ontIds[j]+".owl", mappingFolder + ontIds[i] + "-" + ontIds[j] + ".txt" );
				cm.createAlignment();
				cm.writeAlignment("exp/complexAlignmentsConferenceExtended/" + ontIds[i] + " - " + ontIds[j]+ ".txt");
			}
		}
	}	
}
