
package pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLEntity;

import utility.Attributes;
import de.unima.alcomox.mapping.Correspondence;
import de.unima.ki.mmatch.MMatchException;
import de.unima.ki.mmatch.descriptions.Term;
import de.unima.ki.mmatch.smeasures.Measure;
import exception.ComplexMappingException;
import functions.entityComparison.SubclassOf;

/**
 *
 * @author Dominique Ritze
 *
 * In this class, the selection of correspondences for the resulting mapping
 * is implemented. If a 1:1 mapping is requested, it is necessary to check
 * which complex correspondences are written into the mapping.
 * Therefore the reference alignment must also be taken into account, because
 * if an entity is already in the reference alignment, it should not
 * be in the complex alignment.
 *
 */
public class Selector {

    private Pattern p;

    /**
     * Selects the correspondences from a given list of correspondences, which
     * can be in a 1:1 mapping. Also the correspondences from the reference alignment
     * are taken into account.
     *
     *
     * @param correspondences All correspondences for a pattern.
     * @return A list with the correspondences which are in the resulting 1:1 mapping.
     * @throws ComplexMappingException
     */
    public ArrayList<pattern.ComplexCorrespondence> selectCorres(ArrayList<ComplexCorrespondence> correspondences) throws ComplexMappingException{
        
        ArrayList<ComplexCorrespondence> currentCorres;
        currentCorres = deleteSimpleCorres(correspondences);
        //currentCorres = deleteSubCorres(currentCorres);
        currentCorres = selectBestComplexCorres(currentCorres);
        
        return currentCorres;
    }

    public ArrayList<pattern.ComplexCorrespondence> selectBestComplexCorres(ArrayList<ComplexCorrespondence> correspondences) throws ComplexMappingException {
        ArrayList<ComplexCorrespondence> bestCorres = new ArrayList<ComplexCorrespondence>();
        if(correspondences.size() > 0) {
            this.p = correspondences.get(0).getPattern();
        }
        else {
            return correspondences;
        }
        Map<OWLEntity, ArrayList<ComplexCorrespondence>> sepEntityToCorres = new HashMap<OWLEntity, ArrayList<ComplexCorrespondence>>();
        for(ComplexCorrespondence currentCorres : correspondences) {
            //determine identifier of separated entity
            String separatedIdent = determineSeparatedEntity(currentCorres);
            int index = p.getAssignment().get(separatedIdent);
            OWLEntity separatedEnt = currentCorres.getTuple().getEntries()[index];
            //fill the map with key: separated entity and value a list of correspondences
            //which share the same separated entity
            if(!sepEntityToCorres.keySet().contains(separatedEnt)) {
                ArrayList<ComplexCorrespondence> corresToSepEntity = new ArrayList<ComplexCorrespondence>();
                corresToSepEntity.add(currentCorres);
                sepEntityToCorres.put(separatedEnt, corresToSepEntity);
            }
            else {
                sepEntityToCorres.get(separatedEnt).add(currentCorres);
            }
        }
        //iterate through the separated entities
        for(OWLEntity ent : sepEntityToCorres.keySet()) {
            //just one correspondence with this separated entity
            // -> must not be filtered and can be added to the results
            if(sepEntityToCorres.get(ent).size() == 1) {
                bestCorres.add(sepEntityToCorres.get(ent).get(0));
            }
            //more than one correspondence has the same separated entity
            else {
                boolean[] sepDelimiter;
                boolean[] complexDelimiter;
                //determine the delimiters, needed to compare the labels later
                if(Attributes.firstOntology.getEntities().contains(ent)) {
                    sepDelimiter = Attributes.firstOntology.getDelimiter();
                    complexDelimiter = Attributes.secondOntology.getDelimiter();
                }
                else {
                    sepDelimiter = Attributes.secondOntology.getDelimiter();
                    complexDelimiter = Attributes.firstOntology.getDelimiter();
                }
                try {
                    Term sepEntityTerm;
                    if(ent.isOWLDataProperty() || ent.isOWLObjectProperty()) {
                        sepEntityTerm = new Term(ent.getIRI().getFragment(), sepDelimiter, true);
                    }
                    else {
                        sepEntityTerm = new Term(ent.getIRI().getFragment(), sepDelimiter);
                    }
                    double bestValue = 0.0;
                    Set<ComplexCorrespondence> bestOne = new HashSet<ComplexCorrespondence>();
                    //compute similarity scores between separated entity and the
                    //other part of the complex correspondence and choose the best one
                    for(ComplexCorrespondence oneCorres : sepEntityToCorres.get(ent)) {
                        OWLEntity[] parts = oneCorres.getTuple().getEntries();
                        double similarity = 0.0;
                        for(OWLEntity e : parts) {
                            if(e.getIRI().equals(ent.getIRI())) {
                                continue;
                            }
                            Term complexTerm;
                            if(e.isOWLDataProperty() || e.isOWLObjectProperty()) {
                                complexTerm = new Term(e.getIRI().getFragment(), complexDelimiter, true);
                            }
                            else {
                                complexTerm = new Term(e.getIRI().getFragment(), complexDelimiter);
                            }
                            
                            Measure m = new Measure();
                            similarity += m.compare(sepEntityTerm, complexTerm);
                        }
                        //if two correspondences have the same value, select both
                        if(similarity == bestValue) {
                            bestOne.add(oneCorres);
                        }
                        //check whether the current correspondence is the "best" one
                        else if(similarity > bestValue) {
                            bestValue = similarity;
                            bestOne.clear();
                            bestOne.add(oneCorres);
                        }
                    }
                    bestCorres.addAll(bestOne);
                }catch(MMatchException me) {
                    throw new ComplexMappingException(ComplexMappingException.ExceptionType.MMATCH_EXCEPTION,
                            "Could not compare strings to select the best complex" +
                            "correspondence of several possible ones.", me);
                }
            }
        }
        return bestCorres;
    }

    /**
     * Filter correspondences by the similarity value.
     * Especially used to create the reference alignment because
     * this one should be a 1:1 mapping.
     * The correspondences contained in this alignment are selected
     * by the highest similarity value for each entity.
     *
     * @param correspondences
     * @return
     */
    public ArrayList<pattern.ComplexCorrespondence> deleteCorresBySimValue(ArrayList<ComplexCorrespondence> correspondences) throws ComplexMappingException {
        ArrayList<pattern.ComplexCorrespondence> bestCorres = new ArrayList<ComplexCorrespondence>();
        this.p = correspondences.get(0).getPattern();
        Iterator it = correspondences.iterator();
        //check the possible correspondences for each entity
        OWLEntity firstEntity;
        while(it.hasNext()) {
            firstEntity = ((ComplexCorrespondence)it.next()).getTuple().getEntries()[0];
            ComplexCorrespondence bestCorrespondence = null;
            double maxValue = 0;
            OWLEntity bestEntity = null;
            //check every correspondence to determine the "best" one
            for(ComplexCorrespondence c : correspondences) {
                OWLEntity currentEntity;
                //the first or second entity can be the "current" entity
                if(c.getTuple().getEntries()[0].getIRI().equals(firstEntity.getIRI()) ||
                        c.getTuple().getEntries()[1].getIRI().equals(firstEntity.getIRI())) {
                    //check whether the similarity value is higher than
                    //all other regarded before
                    if(c.getTuple().getSimilarityValue() >= maxValue) {
                        //save the current entity
                        if(c.getTuple().getEntries()[0].getIRI().equals(firstEntity.getIRI())) {
                            currentEntity = c.getTuple().getEntries()[1];
                        }
                        else {
                            currentEntity = c.getTuple().getEntries()[0];
                        }
                        //check if the current entity has the same similarity value
                        //than the best similarity value and the current entity
                        //is a superclass of the best entity, e.g.
                        //in case Conference_Participant and Early-Registered_Participant
                        //compared with Participant
                        SubclassOf sc = new SubclassOf();
                        if(bestEntity != null &&  c.getTuple().getSimilarityValue() == maxValue && sc.compute(bestEntity, currentEntity)) {
                            maxValue = c.getTuple().getSimilarityValue();
                            bestEntity = currentEntity;
                            bestCorrespondence = c;
                            continue;
                        }
                        //the other way round
                        if(bestEntity != null &&  c.getTuple().getSimilarityValue() == maxValue && sc.compute(currentEntity, bestEntity)) {
                            continue;
                        }

                        maxValue = c.getTuple().getSimilarityValue();
                        if(c.getTuple().getEntries()[0].getIRI().equals(firstEntity.getIRI())) {
                            bestEntity = c.getTuple().getEntries()[1];
                        }
                        else {
                            bestEntity = c.getTuple().getEntries()[0];
                        }
                        bestCorrespondence = c;
                    }
                }
            }
            bestCorres.add(bestCorrespondence);
        }
        return bestCorres;
    }

    /**
     * Delete all correspondences where the separated entity
     * is also contained in the reference alignment.
     *
     * @param correspondences
     * @return
     */
    private ArrayList<ComplexCorrespondence> deleteSimpleCorres(ArrayList<ComplexCorrespondence> correspondences) {
                ArrayList<ComplexCorrespondence> selectedCorrespondences = new ArrayList<ComplexCorrespondence>();
                String separatedEntityIdent = determineSeparatedEntity(correspondences.get(0));

                ArrayList<de.unima.alcomox.mapping.Correspondence> simpleCorres;
                if(Attributes.alignment != null) {
                    simpleCorres = Attributes.alignment.getAlignment().getCorrespondences();
                }
                else {
                    simpleCorres = new ArrayList<Correspondence>();
                }
                

                for(ComplexCorrespondence c : correspondences) {
                    ArrayList<Integer> indexes = new ArrayList<Integer>();
                    if(c.getTuple().getEntries().length == 2 || c.getPattern().getPartsOfCorrespondence().size() == 2) {                       
                        indexes.add(0);
                        indexes.add(1);
                    }
                    else if(c.getPattern().getPartsOfCorrespondence().size() == 2) {
                        indexes.add(c.getPattern().getAssignment().get(c.getPattern().getPartsOfCorrespondence().get(0)));
                        indexes.add(c.getPattern().getAssignment().get(c.getPattern().getPartsOfCorrespondence().get(1)));
                    }
                    else {
                        indexes.add(p.getAssignment().get(separatedEntityIdent));
                    }
                    boolean inSimpleAlig = false;
                    for(int index : indexes) {
                        OWLEntity separatedEnt = c.getTuple().getEntries()[index];
                        for(de.unima.alcomox.mapping.Correspondence simpleCor : simpleCorres) {
                            if(separatedEnt.getIRI().toString().equals(simpleCor.getSourceEntityUri().toString()) ||
                               separatedEnt.getIRI().toString().equals(simpleCor.getTargetEntityUri().toString())) {
                                inSimpleAlig = true;
                                break;
                            }
                        }
                        if(!inSimpleAlig) {
                            selectedCorrespondences.add(c);
                            break;
                        }
                    }
                }
                return selectedCorrespondences;
    }

    /**
     *
     * Delete all correspondences whereby the separated entity is the same,
     * another entity also but the third one is different and a subclass
     * from a "third" entity of another correspondence.
     * Currently not used.
     *
     * @param correspondences
     * @return
     * @throws ComplexMappingException
     */
    private ArrayList<ComplexCorrespondence> deleteSubCorres(ArrayList<ComplexCorrespondence> correspondences) throws ComplexMappingException {
                ArrayList<ComplexCorrespondence> selectedCorrespondences = new ArrayList<ComplexCorrespondence>();
                ArrayList<ComplexCorrespondence> subCorrespondences = new ArrayList<ComplexCorrespondence>();
                String separatedEntityIdent = determineSeparatedEntity(correspondences.get(0));
                ArrayList<OWLEntity> separatedEntities = new ArrayList<OWLEntity>();

                for(ComplexCorrespondence c : correspondences) {
                    separatedEntities.add(c.getTuple().getValue(p.getAssignment().get(separatedEntityIdent)));
                }

                for(ComplexCorrespondence c : correspondences) {
                    if(separatedEntities.indexOf(c.getTuple().getValue(p.getAssignment().get(separatedEntityIdent)))
                            == separatedEntities.lastIndexOf(c.getTuple().getValue(p.getAssignment().get(separatedEntityIdent)))) {
                        selectedCorrespondences.add(c);
                    }
                    else {
                        subCorrespondences.add(c);
                    }
                }

                for(ComplexCorrespondence c : subCorrespondences) {
                    for(ComplexCorrespondence d : subCorrespondences) {
                        if(c.equals(d)) {
                            continue;
                        }
                        for(int i=0; i<c.getTuple().getEntries().length; i++) {
                            if(i==p.getAssignment().get(separatedEntityIdent)) {
                                continue;
                            }
                            if(c.getTuple().getValue(i).equals(d.getTuple().getValue(i))) {
                                String identifier = "";
                                for(String s : p.getAssignment().keySet()) {
                                    if(p.getAssignment().get(s) == i) {
                                        identifier = s;
                                    }
                                }
                                if(p.getPartsOfCorrespondence().contains(identifier)) {
                                    for(int j=0; j<c.getTuple().getEntries().length; j++) {
                                            if(j==i) {
                                                continue;
                                            }
                                            if(j==p.getAssignment().get(separatedEntityIdent)) {
                                                continue;
                                            }
                                            String identifier2 = "";
                                            for(String s : p.getAssignment().keySet()) {
                                                if(p.getAssignment().get(s) == j) {
                                                    identifier2 = s;
                                                }
                                            }
                                            if(p.getPartsOfCorrespondence().contains(identifier2)) {
                                                SubclassOf sc = new SubclassOf();
                                                if(sc.compute(d.getTuple().getValue(j),
                                                c.getTuple().getValue(j))) {
                                                    selectedCorrespondences.add(c);
                                                }
                                            }
                                        }
                                }
                            }
                        }
                    }
                }
            return selectedCorrespondences;
    }

    /**
     * Determines the entity of the correspondence which stands alone and is not
     * complex part.
     *
     * @param c
     * @return
     */
    private String determineSeparatedEntity(ComplexCorrespondence c) {

        String separatedEntity ="";
        String firstOntology = "";
        String secondOntology ="";

        this.p = c.getPattern();
        for(String identifier : p.getPartsOfCorrespondence()) {
            if(p.getIdent().get(identifier).equals(Attributes.firstOntology.getClasses()) ||
                    p.getIdent().get(identifier).equals(Attributes.firstOntology.getObjectProperties()) ||
                    p.getIdent().get(identifier).equals(Attributes.firstOntology.getDatatypeProperties())) {
                if(firstOntology.equals("")) {
                    firstOntology = identifier;
                }
                else {
                    firstOntology = "";
                }
                continue;
            }
            if(p.getIdent().get(identifier).equals(Attributes.secondOntology.getClasses()) ||
                    p.getIdent().get(identifier).equals(Attributes.secondOntology.getObjectProperties()) ||
                    p.getIdent().get(identifier).equals(Attributes.secondOntology.getDatatypeProperties())) {
                if(secondOntology.equals("")) {
                    secondOntology = identifier;
                }
                else {
                    secondOntology = "";
                }
                continue;
            }
        }
        if(firstOntology.isEmpty()) {
            separatedEntity = secondOntology;
        }
        else {
            if(secondOntology.isEmpty()) {
                separatedEntity = firstOntology;
            } else {
                //check if 
                if(p.getIdent().get(firstOntology).equals(Attributes.firstOntology.getObjectProperties()) ||
                        p.getIdent().get(firstOntology).equals(Attributes.firstOntology.getDatatypeProperties())) {
                    separatedEntity = secondOntology;
                }
                else {
                    separatedEntity = firstOntology;
                }
            }
            
        }
        return separatedEntity;

    }

}
