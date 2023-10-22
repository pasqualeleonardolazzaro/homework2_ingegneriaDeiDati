package homework2_ingegneriaDeiDati;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {
	 public static void main(String[] args) throws IOException {
	        String indexPath = "C:\\Users\\paleo\\git\\homework2_ingegneriaDeiDati\\homework2_ingegneriaDeiDati\\index";  // Directory where the indexes will be stored
	        String docsPath = "C:\\Users\\paleo\\git\\homework2_ingegneriaDeiDati\\homework2_ingegneriaDeiDati\\docs";    // Directory containing the .txt files to index
	        Directory indexDirectory = FSDirectory.open(Paths.get(indexPath));
	        
	        Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();
	        CharArraySet stopWords = new CharArraySet(Arrays.asList(".txt"), 
	        true);
	        perFieldAnalyzers.put("name", new StandardAnalyzer(stopWords));
	        perFieldAnalyzers.put("content", new WhitespaceAnalyzer());
	        Analyzer analyzer = new PerFieldAnalyzerWrapper(new EnglishAnalyzer(), 
	        perFieldAnalyzers);

	        
	        IndexWriterConfig config = new IndexWriterConfig(analyzer);
	        config.setCodec(new SimpleTextCodec());
	        
	        IndexWriter writer = new IndexWriter(indexDirectory, config);
	        writer.deleteAll();
	        
	        File docsDirectory = new File(docsPath);
	        
	        File[] files = docsDirectory.listFiles();
	        
	        for (File f : files ) {
	        	
	        	Document doc = new Document();
	        	doc.add(new TextField("name", f.getName(), Field.Store.YES));
	        	
	        	StringBuilder content = new StringBuilder();
	        	
	        	//scriviamoci il contenuto del file in una stringa per inserirlo nel documento da indicizzare
	        	try (FileInputStream fis = new FileInputStream(f);
	                    InputStreamReader isr = new InputStreamReader(fis);
	                    BufferedReader br = new BufferedReader(isr)) {

	                   String line;

	                   //Leggi il contenuto del file riga per riga
	                   while ((line = br.readLine()) != null) {
	                       content.append(line).append("\n");
	                   }

	               } catch (IOException e) {
	                   e.printStackTrace();
	               }
	        	
	        	//aggiungo il contenuto
	        	doc.add(new TextField("content", content.toString(), Field.Store.YES));
	        	writer.addDocument(doc);
	        	writer.commit();
	        	
            }
	        
	        writer.close();
	        indexDirectory.close();
	    }
}
