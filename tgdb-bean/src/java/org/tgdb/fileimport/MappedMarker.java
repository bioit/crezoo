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


  Revision 1.1  2001/04/24 09:34:20  frob
  Moved file import classes to new package se.prevas.arexis.util.FileImport,
  caused updates in several files.

  Revision 1.2  2001/04/24 06:31:48  frob
  Checkin after merging frob_fileparser branch.

  Revision 1.1.2.1  2001/04/03 10:31:39  frob
  Class created.

*/

package org.tgdb.fileimport;

import java.util.*;



/**
 * This class is used to represent a marker when a unified marker mapping
 * file is read.
 *
 * @author frob
 * @see Object
 */
class MappedMarker extends Object
{

   /**
    * The name of the sampling unit this mapped marker belong to. 
    */
   private String mSamplingUnit;
   
   /**
    * The name of this marker
    */
   private String mMarkerName;

   /**
    * The alleles included in this object.
    */
   private Vector mAlleles;


   //////////////////////////////////////////////////////////////////////
   //
   // Constructors
   //
   //////////////////////////////////////////////////////////////////////


   public MappedMarker(String suName)
      throws AssertionException
   {
      Assertion.assertMsg(suName != null && suName.length() > 0,
                       "MappedMarker can not be created from a " + 
                       "null/empty string");
      samplingUnit(suName);
      mAlleles = new Vector();
   }
      
   
   //////////////////////////////////////////////////////////////////////
   //
   // Public section
   //
   //////////////////////////////////////////////////////////////////////


   /**
    * Returns the name of the sampling unit this marker belong to.
    *
    * @return The name of the sampling unit.
    */
   public String samplingUnit()
   {
      return mSamplingUnit;
   }


   /**
    * Sets the name of the marker.
    *
    * @param markerName The marker name.
    */
   public void marker(String markerName)
   {
      mMarkerName = markerName;
   }


   /**
    * Returns the name of the marker.
    *
    * @return The name of the marker.
    */
   public String marker()
   {
      return mMarkerName;
   }
      

   /**
    * Adds an allele to the marker
    *
    * @param allele The name of an allele.
    */
   public void addAllele(String allele)
   {
      mAlleles.add(allele);
   }


   /**
    * Returns the number of alleles in this marker.
    *
    * @return Number of alleles
    */
   public int alleles()
   {
      return mAlleles.size();
   }


   /**
    * Returns the allele with the given index
    *
    * @param index The index of an allele to return.
    * @return The name of an allele.
    */
   public String allele(int index)
   {
      return mAlleles.elementAt(index).toString();
   }
      
      
   /**
    * Returns itself in the form of a Mapper object
    *
    * @return a Mapper value
    */
   public Mapper asMapper()
   {
      // Create a new mapper object 
      Mapper aMapper = new Mapper(alleles());

      // Copy marker- and sampling unit name to new Mapper object 
      aMapper.putMarker(marker());
      aMapper.putSUName(samplingUnit());

      // Copy all alleles to the new object and return it.
      for (int i = 0; i < alleles(); i ++)
      {
         aMapper.putAllele(allele(i));
      }
      return aMapper;
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
    * Sets the name of the sampling unit this marker belong to.
    *
    * @param suName The name of the sampling unit.
    */
   private void samplingUnit(String suName)
   {
      mSamplingUnit = suName;
   }
      
}

