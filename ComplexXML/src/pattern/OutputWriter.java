
package pattern;

import exception.ComplexMappingException;
import exception.ComplexMappingException.ExceptionType;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import utility.Attributes;

/**
 *
 * @author Dominique Ritze
 *
 * In this class the mapping is written into the specified output file
 * according to the output format.
 *
 */
public class OutputWriter {

    private FileWriter fw;
    private Selector s;

    /**
     * Write the correspondences into the file
     *
     * @param correspondences
     * @throws ComplexMappingException
     */
    public void writeOutputfile(ArrayList<ComplexCorrespondence> correspondences) throws ComplexMappingException {
        
        BufferedWriter bw = null;

        try {

            //no correspondence found
            if(correspondences.size() == 0) {
                fw = new FileWriter(utility.Attributes.outputFile, true);
                fw.flush();
                fw.close();
                return;
            }

            if(!Attributes.correspondencePattern.get(0).equals(correspondences.get(0).getPattern())) {
                fw = new FileWriter(utility.Attributes.outputFile, true);
            }
            else {
                fw = new FileWriter(utility.Attributes.outputFile, false);
            }

             bw = new BufferedWriter(fw);

            if(correspondences.get(0).getPattern().getSelectAll() == false) {
                s = new Selector();

                ArrayList<ComplexCorrespondence> listOfCorres = s.selectCorres(correspondences);

                if(listOfCorres.size()!=0) {
                    bw.write(listOfCorres.get(0).getPattern().getOutputFormat().getHeader());
                }

                for (ComplexCorrespondence c : listOfCorres) {
                    bw.write(c.toString());
                }
                if(listOfCorres.size()!=0) {
                    bw.write(listOfCorres.get(0).getPattern().getOutputFormat().getFooter());
                }
            }
            else {
                 if(correspondences.size() != 0) {
                     bw.write(correspondences.get(0).getPattern().getOutputFormat().getHeader());
                 }

                for (ComplexCorrespondence c : correspondences) {
                    bw.write(c.toString());
                }
                if(correspondences.size()!=0) {
                    bw.write(correspondences.get(0).getPattern().getOutputFormat().getFooter());
                }
            }
            
            bw.close();
        } catch (IOException ex) {
            throw new ComplexMappingException(ExceptionType.IO_EXCEPTION,
                    "Cannot open or write into output file.", ex);
        }
    }

}
