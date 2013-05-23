package ontology;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLEntity;

import pattern.ComplexCorrespondence;
import pattern.Pattern;
import pattern.Selector;
import utility.Attributes;
import utility.Tuple;
import condition.Condition;
import de.unima.alcomox.exceptions.CorrespondenceException;
import de.unima.alcomox.exceptions.MappingException;
import de.unima.alcomox.mapping.Alignment;
import de.unima.alcomox.mapping.AlignmentReader;
import de.unima.alcomox.mapping.AlignmentReaderTxt;
import de.unima.alcomox.mapping.AlignmentReaderXml;
import de.unima.alcomox.mapping.Correspondence;
import de.unima.alcomox.mapping.SemanticRelation;
import exception.ComplexMappingException;
import exception.ComplexMappingException.ExceptionType;

/**
 * 
 * @author Dominique Ritze
 * 
 * This class is used to set and get the path of an alignment and also
 * to read in the alignment.
 *
 */
public class OntologyAlignment {

	private String path;
        Alignment reference;
	
	/**
	 * Constructor to set the path.
	 * 
	 * @param path
	 */
	public OntologyAlignment(String filepath) {
		this.path = filepath;
	} 
	
	/**
	 * Constructor to set alignment path as URI.
	 * 
	 * @param path
	 */
	public OntologyAlignment(URI path) {
	//	this.path = path;
	}

        /**
         * Create a reference alignment if the according "simple" correspondence
         * pattern has been defined as correspondence_anchor.
         * Therefore all correspondences of the given pattern are computed
         * and build the alignment.
         *
         * @param p
         * @throws ComplexMappingException
         */
        public OntologyAlignment(Pattern p) throws ComplexMappingException {            
            this.reference = new Alignment(getCorrespondence(p));
        }


        /**
         * It is possible that several different correspondence anchors are
         * defined, e.g. to get correspondences between concepts and also
         * between properties. If another anchor has been identified, this one
         * must be joined with all other before. The result is just the union
         * of all correspondences.
         *
         * @param p
         * @throws ComplexMappingException
         */
        public void joinAlignments(Pattern p) throws ComplexMappingException {
            ArrayList<Correspondence> oldCorres = reference.getCorrespondences();
            ArrayList<Correspondence> newCorres = getCorrespondence(p);
            Set<Correspondence> allCorres = new HashSet<Correspondence>();
            //build the union
            allCorres.addAll(oldCorres);
            allCorres.addAll(newCorres);
            this.reference = new Alignment(allCorres);
        }

        /**
         *
         * @param p
         * @return
         * @throws ComplexMappingException
         */
        private ArrayList<Correspondence> getCorrespondence(Pattern p) throws ComplexMappingException {
            ArrayList<Correspondence> corres = new ArrayList<Correspondence>();
            //compute the correspondences accoring to the pattern
            p.computeCorrespondences();
            //filter the correspondences to get a 1:1 mapping
            Selector s = new Selector();
            ArrayList<ComplexCorrespondence> selectedCorres = s.deleteCorresBySimValue(p.getCorrespondences());
            for(ComplexCorrespondence c : selectedCorres) {
                Tuple t = c.getTuple();
                String sourceEntity ="", targetEntity ="";
                //check the type of correspondence, equivalence of subsumption
                try {
                    SemanticRelation sr = new SemanticRelation(SemanticRelation.EQUIV);

                    for(Condition cond : p.getCondition().getConditions()) {
                        if(cond.getType() == Condition.Type.SUBCLASS) {
                            sr = new SemanticRelation(SemanticRelation.SUB);
                        }
                        else {
                            if(cond.getType() == Condition.Type.SUPERCLASS) {
                                sr = new SemanticRelation(SemanticRelation.SUPER);
                            }
                        }
                            sr = new SemanticRelation(SemanticRelation.EQUIV);
                    }
                    //determine source and target entity
                    for(String parts : p.getPartsOfCorrespondence()) {
                        if(sourceEntity.equals("")) {
                            sourceEntity = t.getValue(p.getAssignment().get(parts)).getIRI().toString();
                        }
                        else {
                            targetEntity = t.getValue(p.getAssignment().get(parts)).getIRI().toString();
                        }
                    }
                    //create  new correspondence 
                    corres.add(new Correspondence(sourceEntity, targetEntity, sr, t.getSimilarityValue()));
                } catch (CorrespondenceException ex) {
                throw new ComplexMappingException(ExceptionType.INVALID_REF_ALIGNMENT,
                        "Could not create reference alignment.", ex);
                }
            }
            return corres;
        }
	
	/**
	 * Get the path.
	 * 
	 * @return
	 */
	public String getPath() {
		return this.path;
	}

        /**
         * Get the alignment which has been read in.
         *
         * @return
         */
        public Alignment getAlignment() {
            return this.reference;
        }

        /**
         * Check if the reference alignment is valid, which means in this case that
         * it does not contain any correspondence between datatype and object property.
         *
         * @return False if a correspondence between datatype and objectproperty
         * is in the alignment, true otherwise.
         * @throws ComplexMappingException
         */
        public boolean isValid() throws ComplexMappingException {

                //first read in the alignment
		AlignmentReader mr;

		//test which format is the right one, xml or txt.
		try {
			mr = new AlignmentReaderXml();
			reference = mr.getMapping(Attributes.alignment.getPath());
		} catch(MappingException e) {
			try {
				mr = new AlignmentReaderTxt();
				reference = mr.getMapping(Attributes.alignment.getPath());
			} catch(MappingException me) {
				throw new ComplexMappingException(ExceptionType.MAPPING_EXCEPTION,
                                        "Could not read alignment.", me);
			}
		}

                boolean datatype = false;
                //iterate thorugh the correspondences of the reference alignment and
                //check if an entity of the correspondence is a datatype and the
                //other one an obejctproperty
                for(Correspondence c : reference.getCorrespondences()) {
                    datatype = false;
                    for(OWLEntity e : Attributes.firstOntology.getDatatypeProperties()) {
                        if(c.getSourceEntityUri().equals(e.getIRI().toString()) ||
                                c.getTargetEntityUri().equals(e.getIRI().toString())) {
                            datatype = true;
                        }
                    }
                    for(OWLEntity e : Attributes.secondOntology.getDatatypeProperties()) {
                        if(c.getSourceEntityUri().equals(e.getIRI().toString()) ||
                                c.getTargetEntityUri().equals(e.getIRI().toString())) {
                            datatype = true;
                        }
                    }
                    for(OWLEntity e : Attributes.firstOntology.getObjectProperties()) {
                        if(c.getSourceEntityUri().equals(e.getIRI().toString()) ||
                                c.getTargetEntityUri().equals(e.getIRI().toString())) {
                            if(datatype) {
                                return false;
                            }
                        }
                    }
                    for(OWLEntity e : Attributes.secondOntology.getObjectProperties()) {
                        if(c.getSourceEntityUri().equals(e.getIRI().toString()) ||
                                c.getTargetEntityUri().equals(e.getIRI().toString())) {
                            if(datatype) {
                                return false;
                            }
                        }
                    }
                }
                return true;
        }

}
