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

  Revision 1.1.1.1.2.1  2001/03/27 12:58:31  frob
  Made some previously public data member private and added public methods to access the data.
  Added the log and indented the file correctly.

*/

// Copyright (c) 2000 Prevas AB
package se.arexis.agdb.util.FileImport;

/**
 * Mapps alleles of a marker in a sampling unit to unified alleles of a
 * unified marker. Unified alleles are stored in one array and the alleles
 * of the marker are stored in another array. See example of a mapper
 * object below.
 *
 * <P><PRE>
 *
 *   Sampling_unit: SU001
 *   Marker: MS0101
 *   Unified_marker: M001
 *
 *   Unified_alleles     Alleles
 *    Index  Value     Index Value
 *    +-----+-----+    +-----+-----+
 *    |  0  |  1  |    |  0  |  5  |
 *    +-----+-----+    +-----+-----+
 *    |  1  |  2  |    |  1  |  1  |
 *    +-----------+    +-----------+
 *    |  2  |  3  |    |  2  |  2  |
 *    +-----+-----+    +-----+-----+
 * </PRE>
 *
 * <P>By using the same index when addressing the both arrays, one will
 * find out the mapping. In this example, the unified allele 2
 * (Unified_alleles[1]) in the unified marker M001 is mapped to allele 1 
 * (Alleles[1] in the marker MS0101 in the samlping unit SU001
 * <P>
 * @author <B>Rolf Carlson</B>
 */
public class Mapper
{

   /**
    * The name of the sampling unit 
    */
   private String SU_Name;

   /**
    * The name of the marker in the sampling unit
    */
   private String Marker;

   /**
    * The alleles of the marker
    */
   private String Alleles[];

   /**
    * The name of the unified marker
    */
   private String U_Marker;

   /**
    * The unified alleles of the unified marker
    */
   private String U_Alleles[];

   /**
    * Index used when adding alleles to the allele array
    */
   private int AlleleIndex = 0;
   
   /**
    * Index used when adding unified alleles to the unified allele array
    */
   private int U_AlleleIndex = 0;


   //////////////////////////////////////////////////////////////////////
   //
   // Constructors
   //
   //////////////////////////////////////////////////////////////////////
   

   /**
    * Constructs a new mapper object
    *
    * @param alleleSize The number of alleles this object mapps
    */
   public Mapper(int alleleSize)
   {
      U_Alleles = new String[alleleSize];
      Alleles = new String[alleleSize];
   }


   //////////////////////////////////////////////////////////////////////
   //
   // Public section
   //
   //////////////////////////////////////////////////////////////////////


   /**
    * Gets the name of the sampling unit this mapping object belong to
    *
    * @return The name of the sampling unit
    */
   public String getSUName()
   {
      return SU_Name;
   }
   
   
   /**
    * Sets the name of the sampling unit this mapping object belong to
    *
    * @param Name The name fo the sampling unit
    */
   public void putSUName(String Name)
   {
      SU_Name = Name;
   }


   /**
    * Gets the name of the marker this mapping object belong to
    *
    * @return The name of the marker
    */
   public String getMarker()
   {
      return Marker;
   }
   
   
   /**
    * Sets the name of the marker this mapping object belong to
    *
    * @param Name The name of the marker
    */
   public void putMarker(String Name)
   {
      Marker = Name;
   }
   
   
   /**
    * Gets the name of the unified marker this mapping object mapps alleles
    * to
    *
    * @return The name of the unified marker
    */
   public String getUMarker()
   {
      return U_Marker;
   }
   

   /**
    * Sets the name of the unified marker this mapping object mapps alleles
    * to 
    *
    * @param Name The name of the unified marker
    */
   public void putUMarker(String Name)
   {
      U_Marker = Name;
   }


   /**
    * Returns the number of alleles/unified alleles this object handles
    *
    * @return an int value
    */
   public int alleles()
   {
      return Alleles.length;
   }
   
   
   /**
    * Returns the alleles in this object
    *
    * @return An array with alleles
    */
   public String[] getAlleles()
   {
      return Alleles;
   }


   /**
    * Adds an allele at the current position in the allele array
    *
    * @param Allele The allele to add
    */
   public void putAllele(String Allele)
   {
      Alleles[AlleleIndex] = Allele;
      AlleleIndex ++;
   }


   /**
    * Returns the unified alleles in this object
    *
    * @return An array with unified alleles
    */
   public String[] getUAlleles()
   {
      return U_Alleles;
   }
   

   /**
    * Adds an unified allele at the current position in the unified allele
    * array 
    *
    * @param UAllele The unified allele to add
    */
   public void putUAllele(String UAllele)
   {
      U_Alleles[U_AlleleIndex]= new String(UAllele);
      U_AlleleIndex++;
   }


   //////////////////////////////////////////////////////////////////////
   //
   // Private section
   //
   //////////////////////////////////////////////////////////////////////
   


}

