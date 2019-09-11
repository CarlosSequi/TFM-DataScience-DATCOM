/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prueba3;

// Para usar el clasificador HoeffdingTree directamente sin hacer
// uso de la clase HT3(que es lo mismo pero modificada para el uso de
// restricciones monotónicas)
//import moa.classifiers.trees.HoeffdingTree;
import com.github.javacliparser.FloatOption;
import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.DenseInstance;
import com.yahoo.labs.samoa.instances.Instance;
import moa.classifiers.Classifier;
import moa.core.TimingUtils;
import moa.streams.generators.RandomRBFGenerator;
//import weka.core.Instance;
//import weka.core.Instances;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import moa.core.Measurement;
import weka.core.converters.ArffLoader.ArffReader;
import moa.streams.ArffFileStream;
import prueba3.HT.FoundNode;
import prueba3.HT.Node;
import prueba3.HT.SplitNode;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSink;

public class Prueba3 {

        public Prueba3(){
        }
        
        public boolean instanciasRespetanMonotonía(weka.core.Instance a, weka.core.Instance b)
        {
            boolean aMayorQueB = true;
            boolean bMayorQueA = true;
            boolean iguales = true;
            boolean sonMonotonicos = true;
            
           
            // Comprobamos si todos los atributos de B son mayores o iguales que todos los de A
            for(int i = 0; i<a.numAttributes()-1 && bMayorQueA;i++)
            {
                if(a.value(i) > b.value(i))
                    bMayorQueA = false;  
            }
            
            // Comprobamos si todos los atributos de A son mayores o iguales que todos los de B
            for(int i = 0; i<a.numAttributes()-1 && aMayorQueB;i++)
            {
                if(a.value(i) < b.value(i))
                    aMayorQueB = false;  
            }
            
            // Comprobamos si son iguales
            for(int i = 0; i<a.numAttributes()-1 && iguales;i++)
            {
                if(a.value(i) != b.value(i))
                    iguales = false;  
            }
            
            boolean condicion1 = (bMayorQueA && !aMayorQueB);
            boolean condicion2 = (aMayorQueB && !bMayorQueA);
            
            
            if(condicion1 || condicion2 || iguales)
            {
                sonMonotonicos = true;
            }
           
           
           if(bMayorQueA && (b.value(b.numAttributes()-1) < a.value(a.numAttributes()-1)))
           {
               sonMonotonicos = false;
           }
           else if(aMayorQueB && (b.value(b.numAttributes()-1) > a.value(a.numAttributes()-1)))
           {
               sonMonotonicos = false;
           }
           else if(iguales && (b.value(b.numAttributes()-1) != a.value(a.numAttributes()-1)))
           {
               sonMonotonicos = false;
           }
               
            
            //System.out.print("\n------------\n");
            //System.out.print(a + "\n" + b + "\n" + sonMonotonicos);
            
            return sonMonotonicos;
        }
        
         public boolean instanciasRespetanMonotonía(List<Double> a, List<Double> b, double aClass, double bClass)
        {
            boolean aMayorQueB = true;
            boolean bMayorQueA = true;
            boolean iguales = true;
            boolean sonMonotonicos = true;        
            
           
            // Comprobamos si todos los atributos de B son mayores o iguales que todos los de A
            for(int i = 0; i<a.size()-1 && bMayorQueA;i++)
            {
                if(a.get(i) > b.get(i))
                    bMayorQueA = false;  
            }
            
            // Comprobamos si todos los atributos de A son mayores o iguales que todos los de B
            for(int i = 0; i<a.size()-1 && aMayorQueB;i++)
            {
                if(a.get(i) < b.get(i))
                    aMayorQueB = false;  
            }
            
            // Comprobamos si son iguales
            for(int i = 0; i<a.size()-1 && iguales;i++)
            {
                if(!Objects.equals(a.get(i), b.get(i)))
                    iguales = false;  
            }
            
            boolean condicion1 = (bMayorQueA && !aMayorQueB);
            boolean condicion2 = (aMayorQueB && !bMayorQueA);
            
            
            if(condicion1 || condicion2 || iguales)
            {
                sonMonotonicos = true;
            }
           
           
           if(bMayorQueA && (bClass < aClass))
           {
               sonMonotonicos = false;
           }
           else if(aMayorQueB && (bClass > aClass))
           {
               sonMonotonicos = false;
           }
           else if(iguales && (bClass != aClass))
           {
               sonMonotonicos = false;
           }
               
            
            //System.out.print("\n------------\n");
            //System.out.print(a + "\n" + b + "\n" + sonMonotonicos);
            
            return sonMonotonicos;
        }
        
        public double NMI(String dataset) throws FileNotFoundException, IOException
        {            
            BufferedReader reader = new BufferedReader(new FileReader("D:\\TFM-DataScience-DATCOM\\Experiment\\Data sets\\"+ dataset +"\\"+ dataset +".arff"));
            ArffReader arff = new ArffReader(reader, 1000);
            Instances data = arff.getStructure();
            data.setClassIndex(data.numAttributes() - 1);
            weka.core.Instance inst;
            while ((inst = (weka.core.Instance)arff.readInstance(data)) != null) {
              data.add((weka.core.Instance) inst);
            }
            
            double cantidadChoques = 0.0;
            int cantidadMonotonicos = 0;
            for(int i = 0; i<data.numInstances();i++)
            {
                for(int e = i+1; e<data.numInstances();e++)
                {
                    if(!instanciasRespetanMonotonía(data.get(i), data.get(e)))
                    {
                        cantidadChoques++;
                    }
                    else
                    {
                        cantidadMonotonicos++;
                    }
                }
            }
            
            
            double paresDeEjemplos = data.numInstances()*(data.numInstances()-1);
            //System.out.print("Choques/monotonicos/numInstancias/cantidadParejas: " +cantidadChoques+"/"+cantidadMonotonicos+"/"+data.numInstances()+"/"+paresDeEjemplos);
            double result = (cantidadChoques/paresDeEjemplos);
            //System.out.print(data.get(0).value(2));
            return result;
        }
        
        
        public double NMI(String dataset, List<Double> classPredictions,int numAtributos) throws FileNotFoundException, IOException
        {
           /* BufferedReader reader = new BufferedReader(new FileReader(dataset));
            ArffReader arff = new ArffReader(reader, classPredictions.size());
            Instances data = arff.getStructure();
            data.setClassIndex(data.numAttributes() - 1);
            weka.core.Instance inst;*/
            
            // almacenamos en DATA todos los datos de test 
            ArrayList<Double> instancia = new ArrayList<>(numAtributos);
            ArrayList<ArrayList<Double>>data = new ArrayList<>(0);
            ArffFileStream dataStream = new ArffFileStream(dataset,numAtributos);
            dataStream.prepareForUse();
            // Pasamos el flujo de instancias al contenedor DATA
            while (dataStream.hasMoreInstances())
            {
                double[] arrayInstancia;
                arrayInstancia = dataStream.nextInstance().getData().toDoubleArray();
                for(int i=0; i<arrayInstancia.length; i++)
                {
                    instancia.add(arrayInstancia[i]);
                }
                data.add(instancia);
            }
            
            // CONTAMOS LA CANTIDAD DE CHOQUES QUE HAY ENTRE INSTANCIAS
            double cantidadChoques = 0.0;
            int cantidadMonotonicos = 0;
            for(int i = 0; i<data.size();i++)
            {
                for(int e = i+1; e<data.size();e++)
                {
                    if(!instanciasRespetanMonotonía(data.get(i), data.get(i),classPredictions.get(i),classPredictions.get(e)))
                    {
                        cantidadChoques++;
                    }
                    else
                    {
                        cantidadMonotonicos++;
                    }
                }
            }
            
            
            double paresDeEjemplos = data.size()*(data.size()-1);
            //System.out.print("Choques/monotonicos/numInstancias/cantidadParejas: " +cantidadChoques+"/"+cantidadMonotonicos+"/"+data.numInstances()+"/"+paresDeEjemplos);
            double result = (cantidadChoques/paresDeEjemplos);
            //System.out.print(data.get(0).value(2));
            return result;
        }
               
        public List<List<List<String>>> getBranches(Classifier learner)
        {
            String learnerDescription = learner.toString();
            //System.out.print(learnerDescription);
            String[] lines = learnerDescription.split(System.getProperty("line.separator"));
           List<List<List<String>>> branches = new ArrayList<>();
            boolean claseAlmacenada = false;
            //ArrayList<ArrayList<String>> branch = new ArrayList<>();
            List<List<String>> branch = new ArrayList<>();
            //ArrayList<String> node = new ArrayList<>();
            //List<String> node =  new ArrayList<>();
            for(int i = 0; i<lines.length; i++)
            {                
                    // obtenemos los valores del arbol
                    while(lines[i].contains("if"))
                    {
                        List<String> node = new ArrayList<>();
                        if(lines[i].indexOf("if") != 0)
                        {
                            //System.out.print("\nRemind\n");
                           if(claseAlmacenada)
                           {
                               //System.out.print("\nMenosUno\n");
                               claseAlmacenada = false;
                               List<List<String>> aux = branch;
                               branch = new ArrayList<>();
                               for(int e=0;e<aux.size()-1;e++)
                               {
                                   //System.out.print(branch.get(e));
                                  branch.add(aux.get(e));
                               }
                               //branch.remove(branch.size()-1);
                               /*System.out.print("\n||||||||||\n");
                               System.out.print(branch);
                               System.out.print("\n||||||||||\n");*/
                           }
                           //branch = previousBranch;
                        }
                        else
                        {
                            //System.out.print("\nForget\n");
                            List<List<String>> aux = new ArrayList<>();
                            branch = aux;
                            claseAlmacenada=false;
                        }
                       // System.out.print(lines[i]+"\n"+lines[i].contains("<")+"\n");
                        String attr = lines[i].substring(lines[i].indexOf("att "), lines[i].indexOf("att ")+5);
                        String comparison = "";
                        if(lines[i].contains(" = "))
                            comparison = "=";
                        else
                            comparison = (lines[i].contains("="))? "<=" : ">";
                        
                        String attrValue="";
                        if("=".equals(comparison))
                            attrValue = lines[i].substring(lines[i].indexOf("val ")+4,lines[i].indexOf("val ")+5);
                        else
                        {
                            if(">".equals(comparison))
                                attrValue = lines[i].substring(lines[i].indexOf(comparison)+2,lines[i].indexOf(comparison)+7);
                            else
                                attrValue = lines[i].substring(lines[i].indexOf(comparison)+3,lines[i].indexOf(comparison)+8);
                        }
                        
                        node.add(attr);
                        node.add(comparison);
                        node.add(attrValue);
                        
                        
                        branch.add(node);
                        
                        //System.out.print("\n----------------");
                        //System.out.print("\nA: " + branch.get(0) + "\nC: "+ branch.get(1)+ "\nV: "+branch.get(2)+"\n" );
                        i++;
                    }
                    if(lines[i].contains("class"))
                    {
                        // para eliminar un nodo del previousBranch
                        claseAlmacenada = true;
                        
                        // almacenamos la rama tomada antes
                        // en el vector de ramas
                        branches.add(branch);
                        /*if(branches.size()>1)
                            System.out.print("\n"+branches.get(branches.size()-2));*/
                        
                        // Limpiamos la rama entera
                        //branch = new ArrayList<>();
                        
                        // aqui cogemos la clase de esa rama
                        //System.out.print("\nCLASE");
                    }
                   
            }
             /*for(int a=0;a<branches.size();a++)
                    {
                        
                            System.out.print(branches.get(a)+"\n");
                        
                    }*/
            return branches;
        }
        
         public List<String> getBranchesClasses(Classifier learner)
        {
            String learnerDescription = learner.toString();
            //System.out.print(learnerDescription);
            String[] lines = learnerDescription.split(System.getProperty("line.separator"));
            List<String> branches = new ArrayList<>();
           
            for(int i = 0; i<lines.length; i++)
            {
                    if(lines[i].contains("class"))
                    {
                       // almacenamos el valor de la clase de esa rama
                        branches.add(lines[i].substring(lines[i].indexOf("class ")+6,lines[i].indexOf("class ")+7));
                    }
                   
            }
             /*for(int a=0;a<branches.size();a++)
             {    
                System.out.print(branches.get(a)+"\n");      
             }*/
            return branches;
        }
        
        public boolean branchesRespectMonotonicity(List<List<String>> branchA, List<List<String>>branchB)
        {
            boolean respectConstraints = true;
            
            // significado de cada indice de una rama:
            // 0: atributo
            // 1: desigualdad
            // 2: valor de la desigualdad
            
            // tomamos el tamaño de la rama mas corta para saber cuantas
            // iteraciones hemos de dara para comaparar ambas ramas
            int iteraciones = (branchA.size()>branchB.size()) ? branchB.size() : branchA.size();
            
            // obtenemos la rama mayor de las dos a comparar
            // cuando haya un desvio entre ambas ramas, la que tenga el valor mas alto, esa es la mayor
            int e = 0;
            List<List<String>> ramaMayor = new ArrayList<>();     
            List<List<String>> ramaMenor = new ArrayList<>(); 
            while(e<iteraciones)
            {
                if(branchA.get(e) != branchB.get(e))
                {
                    ramaMayor = (">".equals(branchA.get(e).get(1))) ? branchA : branchB; 
                    ramaMenor = (">".equals(branchA.get(e).get(1))) ? branchB : branchA;
                }
                e++;
            }
            
            
            //System.out.print("\n------------------------------------------------------\n_");
            //System.out.print("Nueva comparacion entre 2 ramas:\n");
            for(int i = 0; i<iteraciones; i++)
            {
                /*System.out.print("\n--------\nNodo "+i+"\n_");
                System.out.print(ramaMenor.get(i).get(0) + "/" + ramaMayor.get(i).get(0) + "_\n_");
                System.out.print(ramaMenor.get(i).get(1) + "/" + ramaMayor.get(i).get(1) + "_\n_");
                System.out.print(ramaMenor.get(i).get(2) + "/" + ramaMayor.get(i).get(2) + "_\n");*/
                
                if(ramaMayor.get(i).get(0).equals(ramaMenor.get(i).get(0)) && ">".equals(ramaMenor.get(i).get(1)) && "<=".equals(ramaMayor.get(i).get(1)) && (ramaMayor.get(i).get(2).equals(ramaMenor.get(i).get(2))))
                {
                    //System.out.print("COLISION");
                    respectConstraints = false;
                }
                    
            }
            
            
            return respectConstraints;
        }
        
        
        public ArrayList<Integer> getClashesMatrix(List<List<List<String>>> branches)
        {
            
            ArrayList<Integer> clashes = new ArrayList<>(Collections.nCopies(branches.size(), 0));
            for(int i=0; i<branches.size(); i++)
            {
                for(int e=i+1; e<branches.size(); e++)
                {
                    // en caso de que no sean monotonicas entre si
                   if(!branchesRespectMonotonicity(branches.get(i),branches.get(e)))
                   {
                       // sumamos a cada rama un choque
                       clashes.set(i, clashes.get(i)+1);
                       clashes.set(e, clashes.get(e)+1);
                   }
                }
            }
            return clashes;
        }
        
        public ArrayList<Integer> getClassClashesMatrix(List<String> branches)
        {
            
            ArrayList<Integer> clashes = new ArrayList<>(Collections.nCopies(branches.size(), 0));
            for(int i=0; i<branches.size(); i++)
            {
                for(int e=i+1; e<branches.size(); e++)
                {
                    // en caso de que no sean monotonicas entre si
                   if(Double.parseDouble(branches.get(i))>Double.parseDouble(branches.get(e)))
                   {
                       // sumamos a cada rama un choque
                       clashes.set(i, clashes.get(i)+1);
                       clashes.set(e, clashes.get(e)+1);
                   }
                }
            }
            return clashes;
        }
        
        public void cantidadInstanciasCadaClase(ArrayList<Double> vectorValoresClase)
        {
             ArrayList<Double> vistos = new ArrayList<>();
                int contador = 0;
                for(int i = 0; i<vectorValoresClase.size();i++)
                {
                    if(!vistos.contains(vectorValoresClase.get(i)))
                    {
                        vistos.add(vectorValoresClase.get(i));
                        for(int e=0;e<vectorValoresClase.size();e++)
                        {
                            if(Objects.equals(vectorValoresClase.get(i), vectorValoresClase.get(e)))
                                contador++;
                        }
                        System.out.print("Clase " + vectorValoresClase.get(i)+": "+contador+"\n");
                    }
                    
                    contador = 0;
                }
        }
        
        public void mostrarRamasArbol(List<List<List<String>>> branches)
        {
            for(int i = 0; i<branches.size(); i++)
                {
                    System.out.print("\n----------------");
                    for(int e=0; e<branches.get(i).size(); e++)
                    {
                        System.out.print("\n" + branches.get(i).get(e));
                    }
                }
        }
        
        public void changeTieThreshold(Classifier learner, double nuevoValor)
        {
            HT arbolito = (HT) learner.getModel();
            FloatOption opcion  = new FloatOption("tieThreshold",
                    't', "Threshold below which a split will be forced to break ties.",
                    1, 0.0, 1.0);
                arbolito.tieThresholdOption = opcion;
        }
        
        public void changeGracePeriodOption(Classifier learner, int nuevoValor)
        {
            HT arbolito = (HT) learner.getModel();
            IntOption opcion  = new IntOption("gracePeriod",
             'g',
             "The number of instances a leaf should observe between split attempts.",
             nuevoValor, 0, Integer.MAX_VALUE);
             arbolito.gracePeriodOption = opcion;
        }
        
        public ArrayList<Double> trainFromData(ArffFileStream stream, Classifier learner, int cantidadIteracionesReaprendizaje,boolean podaActivada)
        {
            ArrayList<Double> classAttributeValues = new ArrayList<>();
            int contadorIteraciones = 0;
            while (stream.hasMoreInstances()) {
                    contadorIteraciones++;
                    if(contadorIteraciones == cantidadIteracionesReaprendizaje)
                    {
                        contadorIteraciones = 0;
                        System.out.print("\n---------------------\n");
                /*HT arbol = (HT) learner.getModel();
                System.out.print(arbol);*/
                        // vemos las ramas que tiene el arbol
                        List<String> branches = getBranchesClasses(learner);
                        System.out.print("\nArray colisiones ramas A: "+getClassClashesMatrix(branches));
                        if(podaActivada)
                            poda(learner);
                /*HT arbol2 = (HT) learner.getModel();
                System.out.print(arbol2);*/
                        List<String> branches2 = getBranchesClasses(learner);
                        System.out.print("\nArray colisiones ramas B: "+getClassClashesMatrix(branches2));
                        
                    }
                        
                    // tomamos el siguiente dato
                    Instance trainInst = stream.nextInstance().getData();
                    classAttributeValues.add(trainInst.classValue());
                    // aprendemos de él
                    //arbolito.trainOnInstance(trainInst);
                    learner.trainOnInstance((com.yahoo.labs.samoa.instances.Instance)trainInst);
                }
            return classAttributeValues;
        }
        
        public ArrayList<Double> testFromData(ArffFileStream testStream, Classifier learner, int numAtributos, String test) throws IOException
        {
            // almacenamos la suma de errores en test
                double sumOfErrors = 0;
                // creamos el contador de instancias del data set
                int cantidadInstanciasTest = 0;
                // contamos la cantidad de aciertos realizados
                int cantidadAciertos = 0;
                // aqui almacenamos las clases predichas por el algoritmo
                // de esta forma podremos calcular su NMI posteriormente
                List<Double> predictedClasses = new ArrayList<>(0);
                // almacenamos los sucesivos NMI de cada paricion
                double NMIs = 0;
                
                // Validamos con el modelo creado anteriormente 
                // y almacenamos los errores cometidos para poder calcular
                // MAE posteriormente
                while (testStream.hasMoreInstances())  {
                    // sumamos la nueva instancia al contador de instancias
                    cantidadInstanciasTest++;
                    // tomamos el siguiente dato
                    Instance testInst = testStream.nextInstance().getData(); //System.out.print(nods); //System.out.print(testInst.);
                    //System.out.print(testInst+"\n"+testInst.classValue()+"\n");
                    // sumamos los errores haciendo la diferencia entre ellos
                    if(!learner.correctlyClassifies(testInst))
                    {
                        // sumamos el error cometido para obtener MAE
                        double error = abs(Utils.maxIndex(learner.getVotesForInstance(testInst)) - (testInst.classValue()));
                        sumOfErrors += error;
                        
                        // PARA HACER COMPROBACIONES
                        /*
                        System.out.print("\n------\n");
                        System.out.print("Instancia numero: "+cantidadInstanciasTest+" --> "+testInst);
                        System.out.print("\nIndice de clase predicho: "+Utils.maxIndex(learner.getVotesForInstance(testInst)) + "\nIndice de clase real:" + testInst.classValue());
                        */
                    }
                    else
                    {
                        cantidadAciertos++;
                        
                        // PARA HACER COMPROBACIONES:
                        // el predictor me da el índice del vector de posibles valores de clase(C) que piensa que
                        // se corresponde con el valor que ha de tener la clase de la instancia predicha.
                        // si c = {3,4,5} (3, 4 y 5 son las distintas clases que existen)
                        // y el predictor me devuelve el valor 2 para una instancia x, entonces es porque
                        // piensa que la etiqueta que ha de tener x es 5 (c[2])
                        /*
                        System.out.print("\n------\n");
                        System.out.print("Instancia numero: "+cantidadInstanciasTest+" --> "+testInst);
                        System.out.print("\nIndice de clase predicho: "+Utils.maxIndex(learner.getVotesForInstance(testInst)) + "\nIndice de clase real:" + testInst.classValue());
                        */
                    }
                    double [] a = learner.getVotesForInstance(testInst);
                    /*System.out.print("INSTANCIA ----> "+testInst+"\n");
                    for(int i = 0; i<a.length; i++)
                    {
                        System.out.print(a[i]+"\n");
                    }
                    System.out.print("---------\n");*/
                    // añadimos la clase predicha al vector de predicciones
                    // para poder sacar el NMI más tarde haciendo uso de él
                    predictedClasses.add((double)Utils.maxIndex(learner.getVotesForInstance(testInst)));
                }
                //System.out.print("\nCantidad de aciertos: " + cantidadAciertos + "/" + cantidadInstanciasTest);
                double MAE = sumOfErrors/cantidadInstanciasTest;
                double NMI = NMI(test,predictedClasses,numAtributos);
                
                ArrayList<Double> result = new ArrayList<>();
                result.add(MAE);
                result.add(NMI);
                
                return result;
        }

        public void poda(Classifier learner)
        {
            HT arbol = (HT) learner.getModel();
            
            // obtenemos los nodos hoja
            HT.FoundNode [] nodosHoja = arbol.findLearningNodes();
            
            // Observamos el arbol inicial
            //System.out.print("\n-------------------");
            //System.out.print("\n"+arbol.getModel()+"\n");
            
            // obtenemos la matriz de colisiones
            List<String> branches = getBranchesClasses(learner);
            ArrayList<Integer> matrizColisiones = getClassClashesMatrix(branches);
            //System.out.print("\nArray colisiones ramas: \n"+matrizColisiones);
            
            // buscamos cual es la hoja que hay que podar
            if(Collections.max(matrizColisiones)>0)
            {
                int ramaAPodar = matrizColisiones.indexOf(Collections.max(matrizColisiones));
                //System.out.print("\nRAMA A PODAR\n"+ramaAPodar);
                
                // realizamos la poda       
                arbol.prune((HT.ActiveLearningNode) nodosHoja[ramaAPodar].node, nodosHoja[ramaAPodar].parent, nodosHoja[ramaAPodar].parentBranch);
            }
            
            
            
            
            //nodosHoja[ramaAPodar].node.;
            // IMPRIMIR CADA UNO DE LOS INDICES DE RAMA
            //HT.FoundNode [] nodosHojaTrasPoda = arbol.findLearningNodes();
            //if(nodosHojaTrasPoda.length == nodosHoja.length)
                //arbol.activateLearningNode((HT.InactiveLearningNode) nodosHojaTrasPoda[ramaAPodar].node, nodosHojaTrasPoda[ramaAPodar].parent, nodosHojaTrasPoda[ramaAPodar].parentBranch);            
            
            //System.out.print("\n"+arbol+"\n");
        }
        
        public void pruebasVarias(Classifier learner)
        {
            HT arbol = (HT) learner.getModel();
            HT.FoundNode [] nodosHoja = arbol.findLearningNodes();
            System.out.print(nodosHoja[0] + " |||");
            //SplitNode nodaso = (SplitNode) arbol.treeRoot;
            
            
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            
            
            /*HT.FoundNode [] nods = arbol.findLearningNodes();
                for(int i =0;i<nods.length;i++)
                {
                    StringBuilder out = new StringBuilder();
                    System.out.print("\nInfo rama " + i + "\n");
                    FoundNode nodoActual = nods[i];
                    int e = 1;
                    while(e < 3)
                    {
                        System.out.print("\n\tInfo nodo " + e + " ------------- " + nodoActual.node+"\n");
                        System.out.print(nodoActual.parent.splitTest.describeConditionForBranch(nodoActual.parentBranch, arbol.getModelContext()) + "\n");
                        
                        nodoActual.node = nodoActual.parent;
                        e++;
                    }
                    //nods[i].node.describeSubtree(arbol, out, 0);
                    //System.out.print("\n::::\n");
                    //System.out.print("\n"+nods[i].parent.subtreeDepth()+"\n");
                    //System.out.print("\n" + arbol);
                    if(nods[i].node.isLeaf() && i == 1 && nods[i].node instanceof HT.ActiveLearningNode)
                    {
                        //System.out.print(nods[i].node.observedClassDistribution + "\n");
                        //System.out.print(nods[i].parent.observedClassDistribution + "\n");
                        //nods[i].node = nods[i].parent;
                        //nods[i].node.
                        //System.out.print(n.observedClassDistribution);
                        //System.out.print("AQUI LERANINNNNNNNNNNNNN");
                        //System.out.print("nodo hoja!\n");
                        //System.out.print(nods[i].node.observedClassDistribution);
                        //nods[i].node.
                        //arbol.deactivateLearningNode(new HT.ActiveLearningNode(nods[i].node.getObservedClassDistribution()) , nods[i].parent, nods[i].parentBranch);
                    }
                }*/
            
        }
        

        public ArrayList<Double> runHT(String train, String test, int numAtributos, int itersReaprendizaje, double tieThreshold,boolean podaActiva) throws FileNotFoundException, IOException, Exception{
            
                // declaramos el clasificador que queremos utilizar
                Classifier learner;
                learner = new HT();
               
                // Cambiamos si queremos el valor de desempate (tie-threshold)
                 changeTieThreshold(learner,tieThreshold);
                 
                // Cambiamos tambien el valore del gracePeriodOption
                // para generar árboles más grandes o más pequeños
                changeGracePeriodOption(learner,itersReaprendizaje);
                
                // declaramos un flujo de datos para train y otro para test
                ArffFileStream trainStream = new ArffFileStream(train,numAtributos);
                ArffFileStream testStream = new ArffFileStream(test,numAtributos);
                trainStream.prepareForUse();
                testStream.prepareForUse();
                
                // establecemos la cabecera de los datos de streaming en el
                // contexto del clasificador, para dejarlo listo para su uso
                learner.setModelContext(trainStream.getHeader());
                learner.prepareForUse();
               // learner.getOptions().addOption(arbolito.tieThresholdOption.setValue(0.35));
                //--------------------------------------------------------------
                // TRAIN
                // primero entrenamos con los datos de train
                // obtenemos el vector de valores del atributo clase (la distribucion)
                ArrayList<Double> classAttributeValues = trainFromData(trainStream,learner,itersReaprendizaje,podaActiva);
                //--------------------------------------------------------------
                
                /*System.out.print("\n---------------------\n");
                HT arbol = (HT) learner.getModel();
                System.out.print(arbol);*/
                
                // vemos la proporcion de instancias de cada clase
                //cantidadInstanciasCadaClase(classAtributeValues);
                
                // comprobamos la cantidad de colisiones que hay entre ramas
                //List<String> branches = getBranchesClasses(learner);
                //System.out.print("\nArray colisiones ramas antes de poda: \n"+getClassClashesMatrix(branches));
                
                // vemos las ramas que tiene el arbol
                //List<List<List<String>>> branches = getBranches(learner);
                // mostrarRamasArbol(branches);

                // Probamos la poda
                //poda(learner);
                //List<String> branches2 = getBranchesClasses(learner);
                //System.out.print("\nArray colisiones ramas despues de poda: \n"+getClassClashesMatrix(branches2));
                // diversas pruebas con el clasificador
                //pruebasVarias(learner);
                
                //--------------------------------------------------------------
                // TEST
                // Preparamos ahora el learner para el testStream
                learner.setModelContext(testStream.getHeader());
                learner.prepareForUse();
                // Obtenemos los resultados
                ArrayList<Double> MAEandNMI = testFromData(testStream,learner,numAtributos,test);
                //--------------------------------------------------------------
                
                return MAEandNMI;
        }
        
        public void HTCrossValidation(String dataset,int numAtributos, int numFolds, int itersReaprendizaje, double tieThreshold,boolean podaActiva) throws IOException, Exception
        {
            // Almacenamos la suma de resultados de cada partición
            double MAEsum = 0;
            double NMIsum = 0;
            for(int i=1; i <= numFolds; i++)
            {
                // declaramos los conjuntos de train y de test
                String train = "D:\\TFM-DataScience-DATCOM\\Experiment\\Data sets\\"+ dataset +"\\"+ dataset +"-10-"+i+"tra.dat";
                String test = "D:\\TFM-DataScience-DATCOM\\Experiment\\Data sets\\"+ dataset +"\\"+ dataset +"-10-"+i+"tst.dat";
                ArrayList<Double> MAEandNMI;
                MAEandNMI = runHT(train, test, numAtributos, itersReaprendizaje,tieThreshold,podaActiva);
                //System.out.print(MAE);
                MAEsum += MAEandNMI.get(0);
                NMIsum += MAEandNMI.get(1);
            }
            
            System.out.print("\nMAE = " + MAEsum/numFolds);
            System.out.print("\nNMI = " + NMIsum/numFolds);
                  
        }

        public static void main(String[] args) throws IOException, Exception {
                Prueba3 exp = new Prueba3();
                // indicamos el data set a usar, la cantidad de atributos
                // (incluyendo al de clase) y el número de folds de la
                // cross validation
                int dataset = 3;
                boolean activarPoda = true;
                int cantidadIteracionesReaprendizaje = 20;
                int cantidadFoldsCrossValidation = 10;
                double tieThreshold = 1;
                switch(dataset) {
                    case 0:
                      exp.HTCrossValidation("era",5,cantidadFoldsCrossValidation,cantidadIteracionesReaprendizaje,tieThreshold,activarPoda);
                      break;
                    case 1:
                      exp.HTCrossValidation("lev",5,cantidadFoldsCrossValidation,cantidadIteracionesReaprendizaje,tieThreshold,activarPoda);
                      break;
                    case 2:
                      exp.HTCrossValidation("esl",5,cantidadFoldsCrossValidation,cantidadIteracionesReaprendizaje,tieThreshold,activarPoda);
                      break;
                    case 3:
                      exp.HTCrossValidation("swd",11,cantidadFoldsCrossValidation,cantidadIteracionesReaprendizaje,tieThreshold,activarPoda);
                      break;
                  }
                
                //Prueba del NMI sobre data sets individuales
                //System.out.print("\nRESULTADO NMI:" + exp.NMI("swd"));
        }
}