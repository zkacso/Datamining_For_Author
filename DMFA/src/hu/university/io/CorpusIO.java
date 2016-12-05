package hu.university.io;

import hu.university.datamining.Corpus;

import java.io.*;

/**
 Created by Zoltán on 2016. 12. 04.. */
public class CorpusIO
{
 public static void SaveWordmapping(Corpus corpus, File output)
 {
  try{
   BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
   int i = 0;
   for(String word : corpus.GetWordSet())
   {
    bw.write("\"" + word + "\";\"" + i + "\"\n");
   }
   bw.close();
  }
  catch (IOException ex)
  {}
 }


 public static void SaveAuthorIds(Corpus corpus, File output)
 {
  try{
  BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
  int i = 0;
  for(String author : corpus.GetAuthors())
  {
   bw.write("\"" + author + "\";\"" + i + "\"\n");
  }
  bw.close();
 }
 catch (IOException ex)
 {}
 }
}
