package org.swip.pivotToMappings.sparql;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class QueryTest {

    public static void main(String[] args) throws FileNotFoundException, IOException {

        long time = System.currentTimeMillis();
        List<String> uris = new LinkedList<String>();
        uris.add("D:/QALDworkshop/musicbrainz/musicbrainz.owl");
        uris.add("D:/QALDworkshop/musicbrainz/artists-veryverysmall.rdf");
//        uris.add("D:/QALDworkshop/musicbrainz/relations_artist_to_artist.rdf");
        LocalSparqlServer serv = new LocalSparqlServer(uris);

        long time2 = System.currentTimeMillis();
        System.out.println("time: " + (time2 - time) + "ms");
        time = time2;

//        System.out.println(serv.isClass("http://musicbrainz.org/mm/mm-2.1#TypeAlbum"));
//
//        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT ?var4  WHERE { <http://musicbrainz.org/mm-2.1/artist/0d4ab0f9-bbda-4ab1-ae2c-f772ffcfbea9> <http://musicbrainz.org/ar/ar-1.0#memberOfBand> ?var3. ?var3 <http://musicbrainz.org/ar/ar-1.0#toArtist> <http://musicbrainz.org/mm-2.1/artist/b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d>. ?var3 <http://musicbrainz.org/mm/mm-2.1#endDate> ?var4.  }";
//
//        System.out.println(query + "\n");
//
//        System.out.println(serv.select(query));
//
//        System.out.println(query + "\n");

//        System.out.println(serv.getALabel("http://musicbrainz.org/mm-2.1/artist/c160a29a-6cbf-4998-862d-e1c6ee3131af"));

        time2 = System.currentTimeMillis();
        System.out.println("time: " + (time2 - time) + "ms");

        java.awt.Toolkit.getDefaultToolkit().beep();
    }
}
