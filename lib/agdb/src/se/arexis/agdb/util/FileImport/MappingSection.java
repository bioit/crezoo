/*
  $Log$
  Revision 1.3  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.2  2002/10/18 11:41:26  heto
  Replaced Assertion.assert with Assertion.assertMsg

  Java 1.4 have a keyword "assert".

  Revision 1.1.1.1  2002/10/16 18:14:07  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.1  2001/04/24 09:34:21  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:49  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.2.1  2001/04/03 10:29:20  frob
  Class created.

*/

package se.arexis.agdb.util.FileImport;

import java.util.*;
import se.arexis.agdb.util.*;


/**
 * This class represents a mapping section of a unified marker mapping
 * file. Each such section hold information about a unified marker and its
 * unified alleles. Furthermore, the section may contain any number of
 * mapped markers, ie markers with alleles mapped to the unified alleles. 
 *
 * @author freob
 * @see Object
 */
public class MappingSection extends Object
{
   
   /**
    * The delimiter char used in the parsed file.
    */
   Character mDelimiter;       

   /**
    * The name of the unified marker read from file.
    */
   String mUnifiedMarkerName;

   /**
    * All unified alleles read from file.
    */
   Vector mUnifiedAlleles;

   /**
    * The markers mapped to this unified marker.
    */
   Vector mMappedMarkers;
         

   
   //////////////////////////////////////////////////////////////////////
   //
   // Constructors
   //
   //////////////////////////////////////////////////////////////////////

   

   /**
    * Creates a new MappingSection instance.
    *
    * @param mappingRow A mapping row, ie the first row of a mapping
    *                   section read from the file. Should start with
    *                   'MAPPING' followed by any number of sampling unit
    *                   names 
    * @param delimiter The character used as delimiter in the parsed file. 
    * @exception AssertionException If object could not be created.
    */
   public MappingSection(String mappingRow, Character delimiter)
      throws AssertionException
   {
      
      // Ensure we have a valid row and delimiter
      Assertion.assertMsg(mappingRow != null && mappingRow.length() > 0,
                       "A MappingSection object has to be " + 
                       "initialized with a non-empty MAPPING row.");

      Assertion.assertMsg(delimiter != null && 
                       delimiter.charValue() != '\0' &&
                       delimiter.charValue() != '\n',
                       "Delimiter must not be null and should only be one "
                       + "character");
      
      // Set the delimiter for future use
      delimiter(delimiter);

      // Build a tokenizer of the row and ensure it is a mapper row
      StringTokenizer aTokenizer =
         new StringTokenizer(mappingRow, delimiter().toString(), false);
      
      Assertion.assertMsg(aTokenizer.nextToken().equalsIgnoreCase("MAPPING"),
                       "Row used to init MappingSection should " +
                       "start with MAPPING");

      // Create the collection of unified alleles and mapped markers.
      mUnifiedAlleles = new Vector();
      mMappedMarkers = new Vector();

      // While ther are more sampling units on the row, create new mapped
      // markers.
      while(aTokenizer.hasMoreTokens())
      {
         createMappedMarker(aTokenizer.nextToken());
      }
   }
   

   //////////////////////////////////////////////////////////////////////
   //
   // Public section
   //
   //////////////////////////////////////////////////////////////////////



   /**
    * Parses an marker row in order to identify the names of one unified
    * marker and a number of mapped markers. For each found mapped marker,
    * a new mapped marker object is created and added to the collection of
    * mapped markers. 
    *
    * @param markerRow The marker row. Should contain the name of a
    *                  unified marker and any number of markers. There
    *                  should be as many markers as there are sampling
    *                  units found on the row used to initalize the object.
    * @param rowNumber The index of the row in the file. Should be the
    *                  actual row number and should NOT be zero based. 
    * @exception AssertionException If columns could not fixed correctly. 
    */
   public void parseMarkerRow(String markerRow, int rowNumber)
      throws AssertionException
   {
      // Ensures the row has a correct number of columns.
      markerRow = ParserUtils.fixColumnsOnRow(markerRow, rowNumber, 
                                              mappedMarkers() + 1, 
                                              delimiter()); 

      // Create a tokenizer based on the row. Each token will represent one
      // marker. 
      StringTokenizer aTokenizer =
         new StringTokenizer(markerRow, delimiter().toString());  
      
      int aTokenIndex = 0;  // Remeber which token (column) in the file we
                            // are operating on

      // While there are more tokens (marker names) on the row
      while (aTokenizer.hasMoreElements())
      {
         // If this is the first token (the first column), we are parsing
         // the unified marker name
         if (aTokenIndex == 0)
         {
            unifiedMarker(aTokenizer.nextToken());
         }

         // If not first token, get the correct mapped marker object and
         // set the marker name of that object
         else
         {
            mappedMarker(aTokenIndex - 1).marker(aTokenizer.nextToken()); 
         }
         aTokenIndex++;
      }
   }
   
            
   /**
    * Parses an allele row in order to identify the names of one unified
    * allele and a number of mapped alleles. There should be as many
    * alleles as there are sampling units and markers. Each found allele is
    * added to the correct marker.
    *
    * @param alleleRow The row to parse.
    * @param rowNumber The index of the row within the file. Should be the
    *                  actual row number and should NOT be zero based.
    * @exception AssertionException If columns could not be fixed correctly.
    */
   public void parseAlleleRow(String alleleRow, int rowNumber)
      throws AssertionException
   {
      // Ensure row has a correct number of columns
      alleleRow = ParserUtils.fixColumnsOnRow(alleleRow, rowNumber,
                                              mappedMarkers() + 1,
                                              delimiter()); 

      // Build a tokenizer based on the row. Each token will represent one
      // allele. 
      StringTokenizer aTokenizer =
         new StringTokenizer(alleleRow, delimiter().toString());


      int aTokenIndex = 0;   // Remember which token (column) in the file
                             // we are operating on
 
      // While there are more alleles
      while(aTokenizer.hasMoreElements())
      {
         // If this is the first token, the value represents a unified
         // allele. Add it to the collection of unified alleles.
         if (aTokenIndex == 0)
         {
            addUnifiedAllele(aTokenizer.nextToken());
         }

         // If not first token, get the correct mapped marker and add the
         // token (allele name) to the marker
         else 
         {
            mappedMarker(aTokenIndex -
                        1).addAllele(aTokenizer.nextToken());
         }
         aTokenIndex++;
      }
   }


   /**
    * Returns the name of the unified marker this object represents.
    *
    * @return The name of the unified marker.
    */
   public String unifiedMarker()
   {
      return mUnifiedMarkerName;
   }
   

   /**
    * Returns the number of mapped markers in this object.
    *
    * @return The number of mapped markers.
    */
   public int mappedMarkers()
   {
      return mMappedMarkers.size();
   }


   /**
    * Returns the mapped marker with the given index.
    *
    * @param index The index of a mapped marker to return.
    * @return The mapped marker with the given index.
    */
   public MappedMarker mappedMarker(int index)
   {
      return (MappedMarker) mMappedMarkers.elementAt(index);
   }


   /**
    * Returns the number of unified alleles in this object
    *
    * @return The number of unified alleles.
    */
   public int unifiedAlleles()
   {
      return mUnifiedAlleles.size();
   }
   

   /**
    * Returns the name of the unified allele with the given index
    *
    * @param index The index of the name to return
    * @return The name of the unified allele
    */
   public String unifiedAllele(int index)
   {
      return  mUnifiedAlleles.elementAt(index).toString();
   }
   

   /**
    * Returns the mapped markers in the section as Mapper objects.
    *
    * @return An array with Mapper objects.
    */
   public Mapper[] asMapper()
   {
      // Create the array used for storing the Mapper objects
      Mapper[] aMapperArray = new Mapper[mappedMarkers()];

      // Variables used in the loop below
      Mapper aMapper;
      MappedMarker aMappedMarker;

      // Loop all mapped markers
      for (int i = 0; i < mappedMarkers(); i++)
      {
         // Get the mapped marker and its Mapper representation Store the
         // Mapper representation in the array of Mappers 
         aMappedMarker = mappedMarker(i);
         aMapper = aMappedMarker.asMapper();
         aMapperArray[i] = aMapper;
         
         // Set the unified marker name of the Mapper object. Also copy
         // the unified alleles to the object.
         aMapper.putUMarker(unifiedMarker());
         for (int x = 0; x < aMappedMarker.alleles(); x++)
         {
            aMapper.putUAllele(unifiedAllele(x));
         }
      }

      // Return the array with Mapper objects
      return aMapperArray;
   }
   

   /**
    * Prints the contents of this section
    *
    */
   public void print()
   {
      System.out.print("MAPPING\t");
      MappedMarker aMappedMarker;

      // Loop all markers within the object. For each marker, print its
      // sampling unit
      for (int i = 0; i < mappedMarkers();  i++) 
      {
         aMappedMarker = mappedMarker(i);
         System.out.print(aMappedMarker.samplingUnit());

         // If there are more markers, print a separator
         if (i + 1 < mappedMarkers())
         {
            System.out.print(";");
         }
      }
      System.out.println();

      // Loop all unified alleles + 1 extra row for marker names
      for (int row = 0; row < mUnifiedAlleles.size() + 1 ; row++)
      {
         // Loop all mappings + 1 extra column for unified marker
         for (int col = 0; col < mappedMarkers() + 1; col++)
         {
            // This is first row, print markers instead of alleles 
            if (row == 0) 
            {
               // This is first column, print the unified marker
               if (col == 0)
               {
                  System.out.print(unifiedMarker());
               }

               // This is not first column, get the correct marker and
               // print its name
               else
               {
                  aMappedMarker = mappedMarker(col - 1);
                  System.out.print(aMappedMarker.marker());
               }
               
               // If there are more markers to print, print separator
               if (col  < mappedMarkers())
               {
                  System.out.print(";");
               }
            }
            
            // This is not first row, print alleles
            else 
            {
               // This is first column, print a unified allele
               if (col == 0)
               {
                  System.out.print(mUnifiedAlleles.elementAt(row -
                                                             1).toString()); 
               }

               // This is not first column, get the correct marker and
               // print its allele
               else
               {
                  aMappedMarker = mappedMarker(col - 1);
                  System.out.print(aMappedMarker.allele(row - 1));
               }

               // If there are more markers to print, print separator
               if (col  < mappedMarkers())
               {
                  System.out.print(";");
               }
            }
         }
         System.out.println();
      }
   }
   


   //////////////////////////////////////////////////////////////////////
   //
   // Protected section
   //
   //////////////////////////////////////////////////////////////////////



   //////////////////////////////////////////////////////////////////////
   //
   // Private section
   //
   //////////////////////////////////////////////////////////////////////
   

   /**
    * Sets the delimiter this mapping section uses.
    *
    * @param delimiter The delimiter
    */
   private void delimiter(Character delimiter)
   {
      mDelimiter = delimiter;
   }


   /**
    * Returns the delimiter of this mapping section.
    *
    * @return The delimiter.
    */
   private Character delimiter()
   {
      return mDelimiter;
   }


   /**
    * Creates a new mapped marker.
    *
    * @param samplingUnit The name of the sampling unit the new mapped
    *                     marker belong to.
    * @exception AssertionException If new mapped marker could not be
    *                               created. 
    */
   private void createMappedMarker(String samplingUnit)
      throws AssertionException
   {
      mMappedMarkers.add(new MappedMarker(samplingUnit));
   }

   
   /**
    * Sets the name of the unified marker this mapping section represents. 
    *
    * @param unifiedMarker The name of the unified marker.
    */
   private void unifiedMarker(String unifiedMarker)
   {
      mUnifiedMarkerName = unifiedMarker;
   }
   
   
   /**
    * Adds an unified allele to the collection.
    *
    * @param alleleName The name of the new unified allele.
    */
   private void addUnifiedAllele(String alleleName)
   {
      mUnifiedAlleles.add(alleleName);
   }

}
