package parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import utility.Attributes;
import exception.ComplexMappingException;
import exception.ComplexMappingException.ExceptionType;

/**
 *
 * @author Dominique Ritze
 *
 * Every pattern can have its on output format which must be read in such
 * that it is available when the correspondences of the pattern are written
 * into the output file.
 *
 */
public class OutputFormat {
	
	private BufferedReader read;
	private int formatNumber;
	private StringBuilder correspondenceFormat;
        private StringBuilder header = new StringBuilder();
        private StringBuilder footer = new StringBuilder();

        /**
         * Constructor for the Output Format.
         *
         * @param xmlFile The file in which the pattern(s) are defined
         * @throws ComplexMappingException
         */
	public OutputFormat(File xmlFile) throws ComplexMappingException {
		try {
			read = new BufferedReader(new FileReader(xmlFile)); 
			formatNumber = Attributes.correspondencePattern.size();
			readOutputFormat();
		} catch(FileNotFoundException e) {
			throw new ComplexMappingException(ExceptionType.IO_EXCEPTION, "",e);
		}
		catch(IOException io) {
			throw new ComplexMappingException(ExceptionType.IO_EXCEPTION, "",io);
		}
		
	}

        /**
         * Read the xml file and save the corresponding lines of the file.
         * If more than one pattern in specified, it must be checked how
         * many pattern have been defined before the current one to
         * get the correct lines.
         *
         * @throws IOException
         */
	private void readOutputFormat() throws IOException {
		String line = read.readLine();
		int numberCounter = -1;
                int numberWhitespaces = 0;
                boolean firstTag = false, headerFinished = true;
		while(line != null) {
			if(line.toLowerCase().contains("<outputformat>")) {
				numberCounter++;
				//<outputfomat> should not be added
				line = read.readLine();
			}
                        if(line.toLowerCase().contains("cdata")) {
                            firstTag = true;
                            //<<!cdata> should not be added
                            line = read.readLine();
			}                       

                        if(line.toLowerCase().contains("]]>")) {
                            line = read.readLine();
                        }

			if(numberCounter == formatNumber) {
                            if(firstTag) {
                                numberWhitespaces = line.indexOf("<");
                                firstTag = false;
                            }

                            if(line.toLowerCase().contains("<correspondenceformat>")) {
                               correspondenceFormat = new StringBuilder();
                               headerFinished = false;
                               line = read.readLine();
				while(!line.toLowerCase().contains("</correspondenceformat>")) {
                                    correspondenceFormat.append(line.substring(numberWhitespaces+1));
                                    correspondenceFormat.append("\n");
                                    line = read.readLine();
				}
                            }
                            else {
                                if(line.toLowerCase().contains("</outputformat>")) {
                                    break;
                                }
                                if(headerFinished) {
                                    header.append(line.substring(numberWhitespaces));
                                    header.append("\n");
                                }
                                else {
                                    footer.append(line.substring(numberWhitespaces));
                                    footer.append("\n");
                                }                               
                            }
			}
			line = read.readLine();
		}
		
	}

        /**
         *
         * @return The output format as string.
         */
	public String getCorrespondenceOutput() {
		return correspondenceFormat.toString();
	}

        public String getHeader() {
            return header.toString();
        }

        public String getFooter() {
            return footer.toString();
        }
	

}
