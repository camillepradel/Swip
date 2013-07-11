package experiments;

import complexMapping.ComplexMapping;
import complexMapping.ComplexMappingException;

import de.unima.alcomox.mapping.Characteristic;
/**
 * 
 * @author Dominique Ritze
 * 
 * Class to test the benchmark ontologies (101,301,302,303,304).
 *
 */
public class Benchmark {
	
	public static void main(String[] args) throws ComplexMappingException {
		
		Characteristic.useDiffuseEvaluation();
		
		String ontFolders[] = {"exp/benchmark/101/", "exp/benchmark/301/", "exp/benchmark/302/", "exp/benchmark/303/", "exp/benchmark/304/"};		
		String mappingId;		
		String ontId = "onto";			
		
		for (int i = 1; i < ontFolders.length; i++) {
			mappingId = ontFolders[0].split("/")[ontFolders[0].split("/").length-1] + "-" + ontFolders[i].split("/")[ontFolders[i].split("/").length-1];
			ComplexMapping cm = new ComplexMapping(ontFolders[0] + ontId + ".rdf", ontFolders[i] + ontId +".rdf", ontFolders[i] + "refalign.rdf");
			cm.createAlignment();
			cm.writeAlignment("exp/complexAlignmentsBenchmark/" + mappingId+ ".txt");
		}
		
	}

}
