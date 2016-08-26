# Ms-Pacman

Proyecto de Sistemas Inteligentes - Ms Pacman.


## Usando Eclipse

Abra la carpeta de ms_pacman para trabajar con el proyecto. La clase 
principal para correrlo es pacman.MsPacInterface.

## Usando Consola

### Compilar

Para compilar, desde la carpeta raíz del proyecto use el siguiente 
comando:

```{bash}
javac pacman/MsPacInterface.java
```

### Para ejecutar

Use el siguiente comando:

```{bash}
java pacman.MsPacInterface
```
##Notas importantes

Se debe procesar la imagen representada por la matríz de enteros int [w][h] dónde w  =?  y h  = ?. 

Se cuenta con los objetos: Ghosts, pills, power pills y Ms.pacman (the Agent).

El agente cuenta con una función que le permite conocer su distancia a cada una de las paredes más cercanas. Estos valores se guardan en un arreglo d. 

Creo que debemos codificar nuestro método en la clase Agent.java modificar move(GameState gs) y eval(vector2d pos , gs) eval recibe como parámetros la posición del agente(pos) y el estado del tablero (gs).

Podemos establecer la posición de inicio de Ms.Pacman. Debe ser el centro de la ventana... Nos encargaríamos de modificar las constantes:

// change these to suit your screen position
static int left = 530;
static int top = 274;

##Recursos
http://cscourse.essex.ac.uk/cig/2005/papers/p1058.pdf
www.csse.uwa.edu.au/cig08/Proceedings/papers/8036.pdf
