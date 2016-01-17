package index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.*;
import com.sun.javafx.binding.StringFormatter;
import java.util.Arrays;
import java.util.Iterator;
import tree.*;
import link.*;

public class InvertedIndex<K extends Comparable<K>, V extends Comparable<V>>
{
	/** Tree used to implement the inverted index */
	private BST<String, FileInfo> index;

	/** Words which will be ignored and not indexed */
	public final static List<String> STOPWORDS = Arrays.asList("a", "able", "about",
			"across", "after", "all", "almost", "also", "am", "among", "an",
			"and", "any", "are", "as", "at", "be", "because", "been", "but",
			"by", "can", "cannot", "could", "dear", "did", "do", "does",
			"either", "else", "ever", "every", "for", "from", "get", "got",
			"had", "has", "have", "he", "her", "hers", "him", "his", "how",
			"however", "i", "if", "in", "into", "is", "it", "its", "just",
			"least", "let", "like", "likely", "may", "me", "might", "most",
			"must", "my", "neither", "no", "nor", "not", "of", "off", "often",
			"on", "only", "or", "other", "our", "own", "rather", "said", "say",
			"says", "she", "should", "since", "so", "some", "than", "that",
			"the", "their", "them", "then", "there", "these", "they", "this",
			"tis", "to", "too", "twas", "us", "wants", "was", "we", "were",
			"what", "when", "where", "which", "while", "who", "whom", "why",
			"will", "with", "without", "would", "yet", "you", "your");
	
	/** Construct an InvertedIndex object
	 * 
	 *  Default constructor
	 */
	/** Words that will not be indexed */
	
	public InvertedIndex()
	{
		index = new BST<>();
	}
	
	/** Construct an InvertedIndex object
	 * 
	 * @param keys
	 * 			array of keys
	 * @param fileInfo
	 * 			container of File and wordPos data
	 */
	
	public InvertedIndex(String[] keys, FileInfo fileInfo)
	{
		index = new BST<>(keys, fileInfo);
	}
	
	public BST<String, FileInfo> getIndex()
	{
		return index;
	}

	public void processFile(File f)
	{
		/*
		 * This regex contains three groups:
		 * group 1 : alphabetic
		 * group 2 : alphabetic ending in number
		 * group 3 : numeric
		 */
		
		String regex = "\\b([a-zA-Z]+)([0-9]*\\b)|\\b([0-9]+)\\b";
		//String regex = "\\W+([a-zA-Z])+[0-9]*\\W+|\\W+([0-9]+)\\W+"; // Try splitting first then substring
		
		Pattern pat = Pattern.compile(regex);
		String[] test = {"eric34 rules-me go get it!", "#34 is my number1.", "'eric's house, @ cool'"};
		Matcher m;
		for(String t : test)
		{
			m = pat.matcher(t);
			while(m.find())
			{
				//System.out.println("match for " + t + " " + m.start() + ":" + m.end());
				for(int i = 0; i <= m.groupCount(); i++)
					System.out.println("group " + i + ": " + m.group(i));
			}
		}	

		try
		{
			Scanner sc = new Scanner(f);			// Read input from file
			int wordPos = 0;						// Keep track of index of word in file
			while(sc.hasNext())
			{
				if(sc.hasNext(pat))
				{
					String word = sc.next(pat).toLowerCase();
					//String word = _word.replaceAll("[^a-zA-Z]", "");		// Replace non-alphabetic text
					if(!STOPWORDS.contains(word) & word != "")			// Insert word if not a stop word	
					{
						FileInfo fi = new FileInfo(f, wordPos);			// FileInfo object to be stored in index 
						index.insert(word, fi);							// as value in BST
						System.out.println(wordPos + "\t" + word);
					}
					System.out.println("pattern found");
				}
				
				else
					sc.next();
					
				wordPos++;		// Advance word pos
			}
				
			sc.close();	
		}
		
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void indexFile(File f)
	{
		try
		{
			Scanner sc = new Scanner(f);			// Read input from file
			int wordPos = 0;						// Keep track of index of word in file
			while(sc.hasNext())
			{
				String _word = sc.next().toLowerCase();
				String word = _word.replaceAll("[^a-zA-Z]", "");		// Replace non-alphabetic text
				if(!STOPWORDS.contains(word) & word != "")			// Insert word if not a stop word	
				{
					FileInfo fi = new FileInfo(f, wordPos);			// FileInfo object to be stored in index 
					index.insert(word, fi);							// as value in BST
					System.out.println(wordPos + "\t" + word);
				}
					
				wordPos++;		// Advance word pos
			}
			
			sc.close();	
		}
		
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void indexFile2(File f)
	{
		System.out.println("indexing file");
		
		/*
		 * A regular expression is used to tokenize text in file
		 * regex contains three groups:
		 * group 1 : alphabetic
		 * group 2 : alphabetic ending in numeral 
		 * group 3 : alpha(numeric) ending in "'s"
		 * group 4 : numeric 
		 *
		 *
		 *
		 *					 (1)			(2)		(3)				(4)	*/
		String regex = "\\b([a-zA-Z]+)([0-9]*)(['’]s)?\\b|\\b([0-9]+)\\b";
		Pattern pat = Pattern.compile(regex);
		Matcher m;
	
		try(BufferedReader reader = new BufferedReader(new FileReader(f)))
		{
			int wordPos = 0;						// Keep track of word # in file
			String line = null;
			while((line = reader.readLine()) != null)
			{
				m = pat.matcher(line);
				/*
				if(m==null)
					System.out.println("m null");
				for(int i = 0; i < m.groupCount(); i++)
					System.out.println(m.group(i));
				*/
				while(m.find())
				{
					//System.out.println("match found in line");

					String _word = null;
					if((		_word=m.group(4)) != null && 
							!_word.equals("") && 
							!STOPWORDS.contains(_word.toLowerCase())	) 			// group 4 (numeric) found
					{
						String word = _word.toLowerCase();
						System.out.println("group 4 " + "'" + _word + "'");
						FileInfo fi = new FileInfo(f, wordPos);					// FileInfo object to be stored in index 
						index.insert(word, fi);									// as value in BST
					}
					
					
					else if((	_word=m.group(1)) != null && 
								!_word.equals("") && 
								!STOPWORDS.contains(_word.toLowerCase())	)		// group 1 (alphabetic) found
					{
						String word = _word.toLowerCase();
						System.out.println("group 1 " + "'" + _word + "'");	
						FileInfo fi = new FileInfo(f, wordPos);					// FileInfo object to be stored in index 
						index.insert(word, fi);									// as value in BST
					
						if((		_word=m.group(2)) != null && 
								!_word.equals("") && 
								!STOPWORDS.contains(_word.toLowerCase()) )		// group 3 (numeric) found and split and indexed also
						{
							word = _word.toLowerCase();
							System.out.println("group 2 " + "'" + _word + "'");	
							fi = new FileInfo(f, wordPos);						// FileInfo object to be stored in index 
							index.insert(word, fi);								// wordPos++ not incremented
														
							/*
							if(!STOPWORDS.contains(word2))
							{
								FileInfo fi2 = new FileInfo(f, wordPos);			// FileInfo object to be stored in index 
								index.insert(m.group(2), fi2);					// wordPos++ not incremented
																				// in "aaa45", both aaa and 45 point to same WordPos
							}*/
																			
						}
						
						// Group 3 ('s) ignored	
						
					}
					wordPos++;	
				}
			}
			
		}
		
	
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
 					 
		
		/* OLDER VERSION
		 * 
		try(BufferedReader reader = new BufferedReader(new FileReader(f)))
		{
			// Read input from file
			
			
			int wordPos = 0;						// Keep track of index of word in file
			String line = null;
			while((line = reader.readLine()) != null)
			{
				for(String word: line.split("[^a-zA-Z0-9]"))
				{
					word = word.toLowerCase();
					if(!stopwords.contains(word) & word != "")			// Insert word if not a stop word	
					{
						FileInfo fi = new FileInfo(f, wordPos);			// FileInfo object to be stored in index 
						index.insert(word, fi);							// as value in BST
						System.out.println(wordPos + "\t" + word);
					}
						
					wordPos++;		// Advance word pos
				}
			}
			
		
			System.out.println("Total words read" + wordPos);
			
		
	
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	*/
	
	public static void main(String[] args)
	{
		String path = "/Users/mambo/Dropbox/code/data.txt";
		File file = new File(path);
		InvertedIndex<String, FileInfo> idx = new InvertedIndex<>();

		try
		{
			//idx.processFile(file);
			idx.indexFile2(file);

			
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		//BST<String, FileInfo> index = idx.getIndex();
		
		idx.index.inorder();
		// Print keys and values
		System.out.println("Index keys are \n" + idx.index.keys());
		System.out.println("Index values are \n" + idx.index.values());
		System.out.println(idx.index.size());
		Bag<FileInfo> bag = idx.index.get("against");
		System.out.println(bag);
		
		/*
		 * Bag<FileInfo> loc = index.get("zeno");
		
		if(loc != null)
			for(FileInfo f : loc)
				System.out.println(f);
				*/
	}
	
}	
	
class FileInfo implements Comparable<FileInfo>
{
	/*
	 * FileInfo container class used to keep track of File objects and 
	 * word position.
	 */
	
	private String fileName;
	private String path;
	private int wordPos;
	
	public FileInfo(File file, int wordPos)
	{
		fileName = file.getName();
		path = file.getAbsolutePath();
		this.wordPos = wordPos;
	}
	
	public int getWordPos()
	{
		return wordPos;
	}
	
	@Override
	public String toString()
	{
		return String.format("%s : %d", fileName, wordPos);
	}
	
	public int compareTo(FileInfo file)
	{
		return path.equals(file.path) ? 0 : -1;	
	}
	
	
	@Override
	public boolean equals(Object ob)
	{
		if(ob == null) return false;
		if(ob.getClass() != File.class) return false;
		FileInfo file = (FileInfo)ob;
		if(!eq(fileName, file.fileName)) return false;
		if(!eq(path, file.path)) return false;
		return true;
	}
 
	private static boolean eq(Object ob1, Object ob2) 
	{
		return ob1 == null ? ob2 == null : ob1.equals(ob2);
	}
}
