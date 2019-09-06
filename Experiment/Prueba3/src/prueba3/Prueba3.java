/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prueba3;

// Para usar el clasificador HoeffdingTree directamente sin hacer
// uso de la clase HT(que es lo mismo pero modificada para el uso de
// restricciones monotónicas)
//import moa.classifiers.trees.HoeffdingTree;
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
import java.util.List;
import java.util.Objects;
import weka.core.converters.ArffLoader.ArffReader;
import moa.streams.ArffFileStream;
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

        public double runHT(String train, String test, int numAtributos, int indiceFold) throws FileNotFoundException, IOException, Exception{
            
                // declaramos el clasificador que queremos utilizar
                Classifier learner = new HT();
                
                // declaramos un flujo de datos para train y otro para test
                ArffFileStream trainStream = new ArffFileStream(train,numAtributos);
                ArffFileStream testStream = new ArffFileStream(test,numAtributos);
                trainStream.prepareForUse();
                testStream.prepareForUse();
                
                // establecemos la cabecera de los datos de streaming en el
                // contexto del clasificador, para dejarlo listo para su uso
                learner.setModelContext(trainStream.getHeader());
                learner.prepareForUse();
                
                //--------------------------------------------------------------
                // TRAIN
                // primero entrenamos con los datos de train
                while (trainStream.hasMoreInstances()) {
                    // tomamos el siguiente dato
                    Instance trainInst = trainStream.nextInstance().getData();
                    // aprendemos de él
                    learner.trainOnInstance((com.yahoo.labs.samoa.instances.Instance)trainInst);
                }
                
                //--------------------------------------------------------------
                // TEST
                // Preparamos ahora el learner para el testStream
                learner.setModelContext(testStream.getHeader());
                learner.prepareForUse();
                
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
                    Instance testInst = testStream.nextInstance().getData();
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
                    
                    // añadimos la clase predicha al vector de predicciones
                    // para poder sacar el NMI más tarde haciendo uso de él
                    predictedClasses.add((double)Utils.maxIndex(learner.getVotesForInstance(testInst)));
                }
                //System.out.print("\nCantidad de aciertos: " + cantidadAciertos + "/" + cantidadInstanciasTest);
                double MAE = sumOfErrors/cantidadInstanciasTest;
                double NMI = NMI(test,predictedClasses,numAtributos);
                double total = MAE + NMI;
                System.out.print("\n-------------------\n");
                System.out.print("MAE = " + MAE);
                System.out.print("\nNMI = " + NMI);
                
                
                // creamos el archivo de predicciones nuevo para poder evaluar el NMI
               /* saver.setInstances(predictedData);
                saver.setFile(new File("D:\\TFM-DataScience-DATCOM\\Experiment\\Data sets\\"+ dataset +"\\"+ dataset +"PREDICTED.arff"));*/
                
                return total;
        }
        
        public double HTCrossValidation(String dataset,int numAtributos, int numFolds) throws IOException, Exception
        {
            // Almacenamos la suma de resultados de cada partición
            double sumOfAccuracies = 0;
            for(int i=1; i <= numFolds; i++)
            {
                // declaramos los conjuntos de train y de test
                String train = "D:\\TFM-DataScience-DATCOM\\Experiment\\Data sets\\"+ dataset +"\\"+ dataset +"-10-"+i+"tra.dat";
                String test = "D:\\TFM-DataScience-DATCOM\\Experiment\\Data sets\\"+ dataset +"\\"+ dataset +"-10-"+i+"tst.dat";
                double MAEandNMI = runHT(train, test, numAtributos,i);
                //System.out.print(MAE);
                sumOfAccuracies += MAEandNMI;
            }
            
            return sumOfAccuracies / numFolds;      
        }

        public static void main(String[] args) throws IOException, Exception {
                Prueba3 exp = new Prueba3();
                // indicamos el data set a usar, la cantidad de atributos
                // (incluyendo al de clase) y el número de folds de la
                // cross validation
                double result = exp.HTCrossValidation("lev",5,10);
                System.out.print("\n10 fold CV: MAE + NMI = "+result+"\n-----------\n");
                
                
                //Prueba del NMI
                //System.out.print("\nRESULTADO NMI:" + exp.NMI("swd"));
        }
}