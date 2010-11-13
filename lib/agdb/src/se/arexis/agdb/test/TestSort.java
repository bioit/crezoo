/*
 * TestSort.java
 *
 * Created on April 1, 2005, 4:45 PM
 */

package se.arexis.agdb.test;

import java.util.ArrayList;
import se.arexis.agdb.db.DbException;


/**
 *
 * @author heto
 */
public class TestSort {
    
    /** Creates a new instance of TestSort */
    public TestSort() {
    }
    
    class Dependency
    {
        public String name;
        public String[] dep;
        
        public Dependency(String name, String[] dep)
        {
            this.name = name;
            this.dep = dep;
        }
    }
    
    private int indexOf(ArrayList<Dependency> sorted, String name)
    {
        for (int i=0;i<sorted.size();i++)
        {
            if (sorted.get(i).name.equals(name))
            {
                return i;
            }
        }
        return -1;
    }
    
    public void print(ArrayList<Dependency> list)
    {
        for (int i=0;i<list.size();i++)
        {
            System.out.print(list.get(i).name+ " : ");
            for (int j=0;j<list.get(i).dep.length;j++)
            {
                System.out.print(list.get(i).dep[j]);
            }
            System.out.println("");
        }
    }
    
    public void sort(ArrayList<Dependency> list) throws DbException
    {
        int i=0;
        
        // Loop limiter, detect circular dependencies
        // If the loop cannot correctly sort the list in 100 loops, the 
        // sorting fails
        int k=0; 
        boolean done = false;
        boolean move = false;
        while (i<list.size())
        {
            move = false;
            Dependency tmp = list.get(i);
            
            
            for (int j=0;j<tmp.dep.length;j++)
            {
                String name = tmp.dep[j];
                int index = indexOf(list,name);

                if (index > i)
                {
                    list.add(i,list.get(index));
                    list.remove(index+1);
                    move=true;
                }
            }
            
            
            if (move==false)
                i++;
            
            k++;     
            
            if (k>100)
                throw new DbException("Circular dependencies. 100 tries to sort list exeeded.");
        }
    }
    
    public void runSort()
    {
        ArrayList<Dependency> list = new ArrayList();

        list.add(new Dependency("INDIVIDUAL",new String[] {}));
        
        
        list.add(new Dependency("MARKER",new String[] {"MARKERSET"}));
        list.add(new Dependency("GENOTYPE",new String[] {"INDIVIDUAL","MARKER"}));
        
        list.add(new Dependency("MARKERSET",new String[] {}));
        
        list.add(new Dependency("VARIABLE",new String[] {"VARIABLESET"}));
        list.add(new Dependency("VARIABLESET",new String[] {}));
        
        list.add(new Dependency("PHENOTYPE",new String[] {"VARIABLE"}));
        
        /*
        
        ArrayList indNames      = new ArrayList();
        ArrayList grpNames      = new ArrayList();
        ArrayList smpNames      = new ArrayList();
        ArrayList varSetNames   = new ArrayList();
        ArrayList uVarSetNames  = new ArrayList();
        ArrayList uVarNames     = new ArrayList();
        ArrayList varNames      = new ArrayList();
        ArrayList pheNames      = new ArrayList();
        ArrayList marSetNames   = new ArrayList();
        ArrayList uMarkSetNames = new ArrayList();
        ArrayList uMarkNames    = new ArrayList();
        ArrayList marNames      = new ArrayList();
        ArrayList genNames      = new ArrayList();
        ArrayList mapNames      = new ArrayList();
        */
        
        try
        {
            sort(list);
        
            print(list);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void alg2() throws DbException
    {
        ArrayList<Dependency> list = new ArrayList();

        list.add(new Dependency("F",new String[] {"D","J"}));
        list.add(new Dependency("G",new String[] {"E","D","F"}));
        list.add(new Dependency("A",new String[] {}));
        list.add(new Dependency("E",new String[] {}));
        list.add(new Dependency("C",new String[] {"B"}));
        list.add(new Dependency("D",new String[] {"B"}));
        list.add(new Dependency("B",new String[] {"A","E"}));
        list.add(new Dependency("H",new String[] {"I"}));
        list.add(new Dependency("I",new String[] {"C"}));
        list.add(new Dependency("J",new String[] {"I","B","F","H"}));
        
        
        
        int i=0;
        
        // Loop limiter, detect circular dependencies
        // If the loop cannot correctly sort the list in 100 loops, the 
        // sorting fails
        int k=0; 
        boolean done = false;
        boolean move = false;
        while (i<list.size())
        {
            move = false;
            Dependency tmp = list.get(i);
            
            // If this object has no dependency then move this to begining of 
            // the list.
            if (tmp.dep.length==0 && i!=0)
            {
                /*
                list.add(0,tmp);
                list.remove(i);
                move=true;
                 */
            }
            else
            {
                for (int j=0;j<tmp.dep.length;j++)
                {
                    String name = tmp.dep[j];
                    int index = indexOf(list,name);
                    
                    if (index > i)
                    {
                        list.add(i,list.get(index));
                        list.remove(index+1);
                        move=true;
                    }
                }
            }
            
            if (move==false)
                i++;
            
            k++;     
            
            if (k>100)
                throw new DbException("Circular dependencies. 100 tries to sort list exeeded.");
        }
        
        print(list);
        
        
    }
    
    public void testThis()
    {
        ArrayList<Dependency> unsorted = new ArrayList();

        unsorted.add(new Dependency("F",new String[] {"D"}));
        unsorted.add(new Dependency("G",new String[] {"E","D","F"}));
        unsorted.add(new Dependency("A",new String[] {}));
        unsorted.add(new Dependency("E",new String[] {}));
        unsorted.add(new Dependency("C",new String[] {"B"}));
        unsorted.add(new Dependency("D",new String[] {"B"}));
        unsorted.add(new Dependency("B",new String[] {"A","E"}));



        ArrayList<Dependency> sorted = new ArrayList();

        boolean done = false;

        int i=0;
        while (!done)
        {
            sorted.add(0,unsorted.get(0));
            unsorted.remove(0);
            
            boolean resort = true; 
            boolean added = false;
            while (resort)
            {
                added=false;
                for (int j=0;j<sorted.get(i).dep.length;j++)
                {
                    String name = sorted.get(i).dep[j];
                    int index = indexOf(unsorted,name);

                    if (indexOf(sorted,name)==-1)
                    {
                        sorted.add(0,unsorted.get(index));
                        added=true;
                        
                    }
                    
                    if (index!=-1)
                        unsorted.remove(index);
                    
                }
                
                if (added==false)
                    resort=false;
                
                i=0;
            }
            
            
            
            print(sorted);
            
            System.out.println("---------");
            
            if (unsorted.size()==0)
                done = true;
            
            //i++;
        }

    }

}
