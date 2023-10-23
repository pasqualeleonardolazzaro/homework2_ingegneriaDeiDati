package homework2_ingegneriaDeiDati;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Scanner;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Searcher {

	public static void main(String[] args) throws IOException {

		String indexPath = "C:\\Users\\paleo\\git\\homework2_ingegneriaDeiDati\\homework2_ingegneriaDeiDati\\index";  // Directory where the indexes will be stored
		Directory indexDirectory = FSDirectory.open(Paths.get(indexPath));
		IndexReader reader = DirectoryReader.open(indexDirectory); //Read access to a Directory via IndexReader
		IndexSearcher searcher = new IndexSearcher(reader); //IndexSearcher lets you search on an IndexReader
		
		//stampa le statistiche dell'index
		System.out.println("Statistiche: \n");
		Collection<String> indexedFields = FieldInfos.getIndexedFields(reader);
        for (String field : indexedFields) {
            System.out.println(searcher.collectionStatistics(field));
        }

		// Crea un oggetto Scanner per leggere da console
		Scanner scanner = new Scanner(System.in);
		String field = null;
		while(true) {
			do {
				System.out.println("Inserisci \"name\" o \"content\" se vuoi cercare rispettivamente sul campo nome o contenuto:");
				// Leggi il campo da console
				field = scanner.nextLine();
			} while (!field.equals("name") && !field.equals("content") );

			String query = null;
			System.out.println("Inserisci il testo della query ora:");
			// Leggi il testo della query da console
			query = scanner.nextLine();

			PhraseQuery.Builder builder = new PhraseQuery.Builder();

			// Dividi la stringa in parole utilizzando uno o più spazi come delimitatori
			String[] parole = query.split("\\s+");

			// costruisci la query
			for (String parola : parole) {
				builder.add(new Term(field, parola.toLowerCase()));
			}

			PhraseQuery phraseQuery = builder.build();
			// Esegui la query
			TopDocs topDocs = searcher.search(phraseQuery, 10); // Cerca i primi 10 documenti corrispondenti
			if(topDocs.scoreDocs.length == 0) {
				System.out.println("Nessun risultato");
			}
			// Stampa i risultati
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				int docId = scoreDoc.doc;
				System.out.println("Documento corrispondente con punteggio: " + scoreDoc.score);
				System.out.println("Nome del documento: " + searcher.doc(docId).get("name"));
			}

			System.out.println("Vuoi inserire un'altra query? y/n");
			if(scanner.nextLine().equals("n")) {
				break;
			}
		}
		scanner.close();
	}

}
