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
import com.yahoo.labs.samoa.instances.Instance;
import moa.classifiers.Classifier;
import moa.core.TimingUtils;
import moa.streams.generators.RandomRBFGenerator;
//import weka.core.Instance;
//import weka.core.Instances;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.Math.abs;
import weka.core.converters.ArffLoader.ArffReader;
import moa.streams.ArffFileStream;
import weka.core.Utils;

public class Prueba3 {

        public Prueba3(){
        }

        public double runHT(String train, String test, int numAtributos) throws FileNotFoundException, IOException{
            
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
                }
                System.out.print("\nCantidad de aciertos: " + cantidadAciertos + "/" + cantidadInstanciasTest);
                double MAE = sumOfErrors/cantidadInstanciasTest;
                
                return MAE;
        }
        
        public double HTCrossValidation(String dataset,int numAtributos, int numFolds) throws IOException
        {
            // Almacenamos la suma de resultados de cada partición
            double sumOfAccuracies = 0;
            for(int i=1; i <= numFolds; i++)
            {
                // declaramos los conjuntos de train y de test
                String train = "D:\\TFM-DataScience-DATCOM\\Experiment\\Data sets\\"+ dataset +"\\"+ dataset +"-10-"+i+"tra.dat";
                String test = "D:\\TFM-DataScience-DATCOM\\Experiment\\Data sets\\"+ dataset +"\\"+ dataset +"-10-"+i+"tst.dat";
                double MAE = runHT(train, test, numAtributos);
                //System.out.print(MAE);
                sumOfAccuracies += MAE;
            }
            
            return sumOfAccuracies / numFolds;      
        }

        public static void main(String[] args) throws IOException {
                Prueba3 exp = new Prueba3();
                // indicamos el data set a usar, la cantidad de atributos
                // (incluyendo al de clase) y el número de folds de la
                // cross validation
                double result = exp.HTCrossValidation("swd",10,10);
                System.out.print("\n-----------\n10 fold CV MAE = "+result);
        }
}