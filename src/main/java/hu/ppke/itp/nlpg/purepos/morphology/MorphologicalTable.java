package hu.ppke.itp.nlpg.purepos.morphology;

import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.docmodel.internal.Token;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MorphologicalTable extends AbstractMorphologicalAnalyzer {
	protected File morphFile;
	protected Map<String, List<String>> morphTable;

	public MorphologicalTable(File file) {
		this.morphFile = file;
		try {
			morphTable = readFile(morphFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected Map<String, List<String>> readFile(File file) throws IOException {
		HashMap<String, List<String>> mTable = new HashMap<String, List<String>>();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)));
		String line;
		while ((line = br.readLine()) != null) {
			String[] cells = line.split("\t");
			if (cells.length > 0) {

				String token = cells[0];
				List<String> anals = new ArrayList<String>();
				for (int i = 1; i < cells.length; ++i) {
					anals.add(cells[i]);
				}
				mTable.put(token, anals);
			}
		}

		return mTable;
	}

	@Override
	public List<String> getTags(String word) {
		return morphTable.get(word);
	}

	@Override
	public List<IToken> analyze(String word) {
		List<String> tags = morphTable.get(word);
		if (tags == null)
			return null;
		List<IToken> ret = new ArrayList<IToken>();
		for (String tag : tags) {
			ret.add(new Token(word, tag));
		}
		return ret;
	}

}
