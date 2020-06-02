package sphinx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;

public class TranscriberDemo {

	public static void main(String[] args) throws Exception {

		// Import the model
		Configuration configuration = new Configuration();
		Boolean adapted_model = true;
		if (adapted_model) {
			configuration.setAcousticModelPath("src/main/resources/ca-es_retrained/acoustic-model-adapt");
			configuration.setDictionaryPath("src/main/resources/ca-es_retrained/pronounciation-dictionary.dict");
			configuration.setLanguageModelPath("src/main/resources/ca-es_retrained/language-model.lm.bin");
		} else if (!adapted_model) {
			configuration.setAcousticModelPath("src/main/resources/ca-es/acoustic-model");
			configuration.setDictionaryPath("src/main/resources/ca-es/pronounciation-dictionary.dict");
			configuration.setLanguageModelPath("src/main/resources/ca-es/language-model.lm.bin");
		}

		StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
		
		// Organize access to datafiles
		String dataPath = "data/";
		File f = new File("data");
		String[] filenames;
		filenames = f.list();
		
		// Transcript each file
		for (String filename : filenames) {
			InputStream stream = new FileInputStream(new File(dataPath + filename));
			stream.skip(44);
			recognizer.startRecognition(stream);

			SpeechResult result;
			fileWriter("<s> ");
			while ((result = recognizer.getResult()) != null) {
				System.out.format("Hypothesis: %s\n", result.getHypothesis());
				fileWriter(result.getHypothesis() + " ");
			}
			fileWriter("<\\s> (" + filename + ")\n");
			recognizer.stopRecognition();
		}
	}
	
	static void fileWriter(String string) {
		try {
			FileWriter myWriter = new FileWriter("hypothesis.transcription", true);
			myWriter.write(string);
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
}