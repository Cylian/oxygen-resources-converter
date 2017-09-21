package tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.transform.TransformerException;

import org.junit.Test;

import com.oxygenxml.html.convertor.trasformers.ContentPrinter;
import com.oxygenxml.html.convertor.trasformers.FilePathGenerator;
import com.oxygenxml.html.convertor.trasformers.MarkdownToDitaTransformer;
import com.oxygenxml.html.convertor.trasformers.TransformerFactoryCreator;
import com.oxygenxml.html.convertor.trasformers.TransformerFactoryCreatorImpl;

public class MdToDitaTest {

	@Test
	public void test() throws TransformerException, IOException {
		String sample = "file:" + File.separator + "D:" + File.separator + "HTMLConvertor" + File.separator + "test-sample"
				+ File.separator + "markdownTest.md";
		String goodSample = "D:" + File.separator + "HTMLConvertor" + File.separator + "test-sample" + File.separator
				+ "goodMdToDita.dita";

		String folder = "D:" + File.separator + "HTMLConvertor" + File.separator + "test-sample";

		TransformerFactoryCreator transformerCreator = new TransformerFactoryCreatorImpl();

		MarkdownToDitaTransformer markdownToDitaTransformer = new MarkdownToDitaTransformer();

		String dita = markdownToDitaTransformer.convert(new URL(sample), null, transformerCreator);

		File fileToRead = FilePathGenerator.generate(sample, "dita", folder);

		try {
			ContentPrinter.prettifyAndPrint(new StringReader(dita), fileToRead, "topic.dtd", "-//OASIS//DTD DITA Topic//EN",
					new TransformerFactoryCreatorImpl());

			System.out.println(goodSample);
			System.out.println(fileToRead.toString());

			assertTrue(FileComparationUtil.compareLineToLine(goodSample, fileToRead.toString()));

		} finally {
			try {
				Files.delete(Paths.get(fileToRead.getPath()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
