/*
  $Log$
  Revision 1.2  2003/05/02 07:58:45  heto
  Changed the package structure from se.prevas.arexis.XYZ to se.arexis.agdb.XYZ
  Modified configuration and source files according to package change.

  Revision 1.1.1.1  2002/10/16 18:14:06  heto
  Import of aGDB 1.5 L3 from Prevas CVS-tree.
  This version of aGDB is migrated to Tomcat from JServ by Tobias Hermansson


  Revision 1.1  2001/05/21 07:54:13  frob
  Initial checkin.

*/

package se.arexis.agdb.db.TableClasses;

import java.lang.*;
import java.sql.*;


/**
 * This class mapps to a Individual object in the database. Each field in
 * the database is mapped to an attribute of this class. The purpose of the
 * class is to simplify the management of Individuals.
 *
 * @author frob
 * @see Object
 */
public class Individual extends Object
{
   /** The id of the individual */
   private String mIid = null;

   /** The identity of the individual */
   private String mIdentity = null;

   /** The alias of the individual */
   private String mAlias = null;

   /** The father id of the individual */
   private String mFatherId = null;

   /** The mother id of the individual */
   private String mMotherId = null;

   /** The sex of the individual */
   private String mSex = null;

   /** The birth date of the individual */
   private String mBirthDate = null;

   /** The status of the individual */
   private String mStatus = null;

   /** The sampling unit id of the individual */
   private String mSamplingUnitId = null;

   /** The user id of the individual */
   private String mUserId = null;

   /** The timestamp of the individual */
   private String mTimeStamp = null;

   /** The comment of the individual */
   private String mComment = null;
   

   //////////////////////////////////////////////////////////////////////
   //
   // Constructors
   //
   //////////////////////////////////////////////////////////////////////


   //////////////////////////////////////////////////////////////////////
   //
   // Public section
   //
   //////////////////////////////////////////////////////////////////////


   /**
    * Prints the contents of the object
    *
    */
   public void print()
   {
      System.err.println("Iid: " + iid());
      System.err.println("Identity: " + identity());
      System.err.println("Alias: " + alias());
      System.err.println("Father id: " + fatherId());
      System.err.println("Mother id: " + motherId());
      System.err.println("Sex: " + sex());
      System.err.println("Birthdate:" + birthDate());
      System.err.println("Status: " + status());
      System.err.println("Sampling Unit: " + samplingUnitId());
      System.err.println("User: " + userId());
      System.err.println("Time stamp: " + timeStamp());
      System.err.println("Comment: " + comment());
   }
   

   /**
    * Returns an empty string if the given string is null. If given string
    * is not null, the original string is returned.
    *
    * @param value The string to evaluate.
    * @return A non null string.
    */
   public static String blankIfNull(String value)
   {
      if (value == null)
      {
         return "";
      }
      return value;
   }


   /**
    * Sets the iid of the individual.
    *
    * @param iid The iid to assign the object.
    */
   public void iid(String iid)
   {
      mIid = iid;
   }

   
   /**
    * Gets the iid of the individual.
    *
    * @return The iid of the individual.
    */
   public String iid()
   {
      return mIid;
   }


   /**
    * Sets the identity of the individual.
    *
    * @param identity The identity to assign the object.
    */
   public void identity(String identity)
   {
      mIdentity = identity;
   }


   /**
    * Gets the identiy of the individual.
    *
    * @return The identity of the individual.
    */
   public String identity()
   {
      return mIdentity;
   }

   
   /**
    * Sets the alias of the object.
    *
    * @param alias The alias to assign the object.
    */
   public void alias(String alias)
   {
      mAlias = alias;
   }

   
   /**
    * Gets the alias of the individual.
    *
    * @return The alias of the individual.
    */
   public String alias()
   {
      return mAlias;
   }
   

   /**
    * Sets the father id of the object.
    *
    * @param fatherId The father id to assign the object.
    */
   public void fatherId(String fatherId)
   {
      mFatherId = fatherId;
   }
   

   /**
    * Gets the father id of the object.
    *
    * @return The father id of the object.
    */
   public String fatherId()
   {
      return mFatherId;
   }


   /**
    * Sets the mother id of the object.
    *
    * @param motherId The mother id to assign the object.
    */
   public void motherId(String motherId)
   {
      mMotherId = motherId;
   }


   /**
    * Gets the mother id of the individual.
    *
    * @return The mother id of the individual
    */
   public String motherId()
   {
      return mMotherId;
   }


   /**
    * Sets the sex of the object.
    *
    * @param sex The sex to assign the object.
    */
   public void sex(String sex)
   {
      mSex = sex;
   }


   /**
    * Gets sex the of the individual.
    *
    * @return The sex of the individual.
    */
   public String sex()
   {
      return mSex;
   }


   /**
    * Sets the birth date of the object.
    *
    * @param birthDate The birth date to assign the object.
    */
   public void birthDate(String birthDate)
   {
      mBirthDate = birthDate;
   }


   /**
    * Gets the birthdate of the individual.
    *
    * @return The birthdate of the individual.
    */
   public String birthDate()
   {
      return mBirthDate;
   }


   /**
    * Sets the status of the object.
    *
    * @param status The status to assign the object.
    */
   public void status(String status)
   {
      mStatus = status;
   }


   /**
    * Gets status the of the individual.
    *
    * @return The status of the individual
    */
   public String status()
   {
      return mStatus;
   }


   /**
    * Sets the sampling unit id of the object.
    *
    * @param samplingUnitId The sampling unit id to assign the object. 
    */
   public void samplingUnitId(String samplingUnitId)
   {
      mSamplingUnitId = samplingUnitId;
   }


   /**
    * Gets the sampling unit id of the individual.
    *
    * @return The sampling unit id of the individual
    */
   public String samplingUnitId()
   {
      return mSamplingUnitId;
   }

   
   /**
    * Sets the user id of the object.
    *
    * @param userId The user id to assign the object.
    */
   public void userId(String userId)
   {
      mUserId = userId;
   }


   /**
    * Gets the user id of the individual.
    *
    * @return The user id of the individual
    */
   public String userId()
   {
      return mUserId;
   }

   /**
    * Sets the time stamp of the object.
    *
    * @param timeStamp The time stamp to assign the object.
    */
   public void timeStamp(String timeStamp)
   {
      mTimeStamp = timeStamp;
   }


   /**
    * Gets the time stamp of the individual.
    *
    * @return The time stamp of the individual.
    */
   public String timeStamp()
   {
      return mTimeStamp;
   }


   /**
    * Sets the comment of the object.
    *
    * @param comment The comment to assign the object.
    */
   public void comment(String comment)
   {
      mComment = comment;
   }

   
   /**
    * Gets the comment of the individual.
    *
    * @return The comment of the individual.
    */
   public String comment()
   {
      return mComment;
   }


   /**
    * Copies information from another object to this object.
    *
    * @param individual The object to copy data from.
    */
   public void copy(Individual individual)
   {
      iid(individual.iid());
      identity(individual.identity());
      alias(individual.alias());
      fatherId(individual.fatherId());
      motherId(individual.motherId());
      sex(individual.sex());
      birthDate(individual.birthDate());
      status(individual.status());
      samplingUnitId(individual.samplingUnitId());
      userId(individual.userId());
      timeStamp(individual.timeStamp());
      comment(individual.comment());
   }
   

   /**
    * Replaces any null fields with an empty string.
    *
    */
   public void replaceNull()
   {
      if (iid() == null)
      {
         mIid = "";
      }
      
      if (identity() == null)
      {
         mIdentity = "";
      }
      
      if (alias() == null)
      {
         mAlias = "";
      }

      if (fatherId() == null)
      {
         mFatherId = "";
      }

      if (motherId() == null)
      {
         mMotherId = "";
      }

      if (sex() == null)
      {
         mSex = "";
      }

      if (birthDate() == null)
      {
         mBirthDate = "";
      }
      
      if (status() == null)
      {
         mStatus = "";
      }
      
      if (samplingUnitId() == null)
      {
         mSamplingUnitId = "";
      }
      
      if (userId() == null)
      {
         mUserId = "";
      }

      if (timeStamp() == null)
      {
         mTimeStamp = "";
      }
      
      if (comment() == null)
      {
         mComment = "";
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

}
