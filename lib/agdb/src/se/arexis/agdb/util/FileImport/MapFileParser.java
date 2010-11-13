/*
  $Log$
  Revision 1.2  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.1.1.1  2002/10/16 18:14:07  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.1  2001/04/24 09:34:20  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:48  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.2.2  2001/04/19 09:50:35  frob
  Renamed Parse() method to parseInputFile() and made the method protected.

  Revision 1.1.2.1  2001/04/03 10:31:06  frob
  Class created, replaces MappingFileParser.

*/

package se.arexis.agdb.util.FileImport;

import java.util.*;
import se.arexis.agdb.util.*;


/**
 * The class parses a UnifiedMarkerMapping file. Such a file contains any 
 * number of mapping sections. Each section contains a unified marker
 * containing a number of unified alleles. Each section may also contain
 * any number of markers which are mapped to the unified marker. 
 *
 * <P>The class expects an input file with the following format:
 *
 * <P><PRE>
 *   otn/ftn/1/;
 *   MAPPING;SU1;SU2;SUn
 *   UMark;Mark1;Mark2;MarkN
 *   UA1;A1;A1;An
 *   UA2;A3;A2;An
 *   UAn;A3;A5;An
 *   MAPPING;SU1;SU2;SU3;SU4;SUn
 * </PRE>
 *
 * <P>The first row is the header row, handled bu the super class. The
 * second row is the "MAPPING" row which indicates a new mapping
 * section. The first column of this row must contain the reserved word
 * "MAPPING". The other columns contains the sampling unit names that are
 * mapped in this section. The next row is the "marker row". The first column
 * holds the name of the unified marker. The other columns holds the
 * names of the markers mapped to the unified mapper. There must be as
 * many markers as there are sampling units. Then follows any number of
 * allele rows. The first column of such a row holds a unified allele. The
 * other columns holds an allele in a unified marker. There must be as many
 * alleles as there are markers.
 *
 * <P>The example above tells us that sampling unit SU2 has a marker named
 * Mark2. Allele A1 of that marker is mapped to the unified allele
 * UA1, while allele A2 of thatmarker is mapped to the unified allele UA2.
 *
 * <P>The preferred way of accessing the data in the object is by access a
 * given MappingSection by using <I>mappingSection()</I>. For backward
 * compatibility, the method <I>asMapper()</I> will return the contents of
 * the file as an array of Mapper objects.
 *
 * @author frob
 * @see AbstractFileParser
 */
public class MapFileParser extends AbstractFileParser
{

   /**
    * The mapping sections found in the file
    */
   private Vector mMappingSections;
   

   //////////////////////////////////////////////////////////////////////
   //
   // Constructors
   //
   //////////////////////////////////////////////////////////////////////
   

   /**
    * Creates a MapFileParser.
    *
    * @param fileName The name of the file to read data from 
    * @exception AssertionException If class could not be constructed. 
    */
   public MapFileParser(String fileName)
      throws AssertionException
   {
      super(fileName);
      mMappingSections = new Vector();
   }


   //////////////////////////////////////////////////////////////////////
   //
   // Public section
   //
   //////////////////////////////////////////////////////////////////////


   /**
    * Returns the number of mapping sections in the file
    *
    * @return The number of mapping sections.
    */
   public int mappingSections()
   {
      return mMappingSections.size();
   }
   

   /**
    * Returns the mapping section with the given index
    *
    * @param index The index of the mapping section to return
    * @return A MappingSection object
    */
   public MappingSection mappingSection(int index)
   {
      return (MappingSection) mMappingSections.elementAt(index);
   }
   

   /**
    * Prints all the mapping sections found in the file
    *
    */
   public void test()
   {
      for (int i = 0; i < mappingSections(); i++)
      {
         mappingSection(i).print();
      }
   }


   /**
    * Prints all the mapping sections found in the file using the Mapping
    * structure. 
    *
    */
   public void testAsMapper()
   {
      Mapper aMap;              // The current Mapper object
      String[] aAlleles;        // The alleles of the current Mapper
      String[] aUAlleles;       // The unified alleles of the current
                                // Mapper 
      
      // Get the Mapper objects
      Mapper[] aMappers = asMapper();

      // Loop all Mappers
      for (int i = 0; i < aMappers.length; i++)
      {
         // For each Mapper, print its sampling unit, marker and unified
         // marker. 
         aMap = aMappers[i];
         System.out.println("SU: " + aMap.getSUName() + " Marker: " +
                            aMap.getMarker() + " UMarker: " +
                            aMap.getUMarker());
         
         // Get the alleles and unified alleles of the Mapper object and
         // print them
         aAlleles = aMap.getAlleles();
         aUAlleles = aMap.getUAlleles();
         for (int x = 0; x < aAlleles.length; x++)
         {
            System.out.println(aUAlleles[x] + " " + aAlleles[x]);
         }
      }
   }
   

   
   /**
    * Returns the contenst of the file in the form of array Mapper
    * objects. Each mapped marker found in the file is represented as one
    * Mapper object within the array. This method is for backward
    * compatibility. Mapper class should preferrable be removed.
    *
    * @return An array with Mapper objects
    */
   public Mapper[] asMapper()
   {
      int aMappedMarkerCount = 0;    // The total number of mapped markers

      // Loop all mapping sections and count the total number of mapped
      // markers  
      for (int i = 0; i < mappingSections(); i++)
      {
         aMappedMarkerCount += mappingSection(i).mappedMarkers();
      }

      // Create the array for the Mapper objects. Should have the same size 
      // as there are mapped markers.
      Mapper[] aAllMappers = new Mapper[aMappedMarkerCount];
            
      int aResultPos = 0;       // The position in the result array to put
                                // the next Mapper object in 
      Mapper[] aSectionAsMapper;// The contents of a MappingSection
                                // represented as Mapper objects

      // Loop all mapping sections found in the file 
      for (int aIndex = 0; aIndex < mappingSections(); aIndex++)
      {
         // Get the Mapper representations of the mapped markers within the
         // current section.
         aSectionAsMapper =
            mappingSection(aIndex).asMapper();
         
         // For each Mapping object in the section, add it to the result
         // array 
         for (int aMappingIndex = 0; aMappingIndex < aSectionAsMapper.length;
              aMappingIndex++)
         {
            aAllMappers[aResultPos++] = aSectionAsMapper[aMappingIndex];
         }
      }

      return aAllMappers;
      
   }

   
   //////////////////////////////////////////////////////////////////////
   //
   // Protected section
   //
   //////////////////////////////////////////////////////////////////////


   
   /**
    * Parses the data read from file. Builds a MappingSection object with
    * related markers for each mapping section found in the file
    *
    * @exception InputDataFileException If anything wrong with the input file.
    */
   protected void parseInputFile()
      throws InputDataFileException
   {
      try
      {
         
         // Run the inherited parse() method to read all rows of the
         // file. Also parses the header section
         super.parseInputFile();

         // Init variables used in loop below
         String aRow = "";
         MappingSection aMappingSection = null;
      
         // Loop all rows read from file
         for (int aRowIndex = 0; aRowIndex < fileRows(); aRowIndex++)
         {
            // Get a row and ensure it is a data row
            aRow = fileData()[aRowIndex];
            if (isDataRow(aRow, aRowIndex))
            {
               // If row is at least 7 chars and contains "MAPPING", it is
               // the first row of a mapping section
               if (aRow.length() >= 7 &&
                   aRow.substring(0,7).equalsIgnoreCase("MAPPING")) 
               {
                  // Create a new mapping object and add it to the
                  // collection of mappingSections
                  aMappingSection =
                     new MappingSection(aRow, fieldDelimiter());
                  addMappingSection(aMappingSection);
                  
                  // Increase row index and parse the next row to get the
                  // marker names
                  aRowIndex++;
                  aMappingSection.parseMarkerRow(fileData()[aRowIndex], 
                                                 aRowIndex + 1);
               }

               // This is not the mapping row, parse alleles
               else
               {
                  aMappingSection.parseAlleleRow(aRow, aRowIndex + 1);
               }
            }
         }
      }
      catch (AssertionException e)
      {
         throw new InputDataFileException(e.getMessage());
      }
   }


   //////////////////////////////////////////////////////////////////////
   //
   // Private section
   //
   //////////////////////////////////////////////////////////////////////


   /**
    * Adds a new mapping section to the collection.
    *
    * @param newMappingSection The mapping section to add.
    */
   private void addMappingSection(MappingSection newMappingSection)
   {
      mMappingSections.add(newMappingSection);
   }
   

   
}
