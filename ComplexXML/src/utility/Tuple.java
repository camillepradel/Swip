package utility;

import java.util.Arrays;

import org.semanticweb.owlapi.model.OWLEntity;

import pattern.Pattern;

/**
 *
 * @author Dominique Ritze
 *
 * Representation of a tuple which consists of an
 * array of entities.
 *
 */
public class Tuple {
	
	private OWLEntity[] entries;
        private double simValue;

        /**
         *
         * @param entries An array with the entities.
         */
	public Tuple(OWLEntity[] entries) {
		this.entries = entries;	
	}

        /**
         * Create a tuple corresponding to a pattern
         * but without entries.
         *
         * @param p The corresponding pattern.
         */
	public Tuple(Pattern p) {
		entries = new OWLEntity[p.getIdent().keySet().size()];
	}	

        /**
         *
         * @return Representation of the tuple entries.
         */
	@Override
	public String toString() {
		return Arrays.toString(entries);
	}

        /**
         *
         * @param o
         * @return True if the entries of the tuples are the same.
         */
	@Override
	public boolean equals(Object o) {
                if(o instanceof Tuple) {
                    if(Arrays.equals(((Tuple)o).entries, this.entries)) {
			return true;
                    }
                    else {
			return false;
                    }
                }
                else {
                    return false;
                }
	}

        /**
        *
        * @return
        */
         @Override
         public int hashCode() {
            int hash = 7;
            hash = 13 * hash + Arrays.deepHashCode(this.entries);
            return hash;
        }

        /**
         *
         * @return The entries of the tuple.
         */
	public OWLEntity[] getEntries() {
		return this.entries;
	}

        /**
         *
         * @return A copy of the entries.
         */
	public Tuple getCopy() {
		return new Tuple(this.entries);
	}

        /**
         * Set an entry to a position.
         *
         * @param position
         * @param value
         */
	public void setValue(int position, OWLEntity value) {
		this.entries[position] = value;
	}

        /**
         * 
         * @param position
         * @return
         */
	public OWLEntity getValue(int position) {
		return this.entries[position];
	}

        public void setSimilarityValue(double value) {
            if(value > this.simValue) {
                this.simValue = value;
            }
        }

        public double getSimilarityValue() {
            return this.simValue;
        }
}
