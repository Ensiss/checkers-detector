/**
 * Masque de convolution (carre).
 * Les coordonnees des elements du masque vont de -radius a +radius.
 */
public class Mask {

    private double[] contenu;
    private int radius;
    private int width;

    /**
    * Cree un nouveau masque de convolution.
    * C'est un carre de cote (2 * radius + 1).
    * Tous les elements sont a zero.
    * @param radius     Radius du masque de convolution.
    */
    public Mask(int radius) {
        this(radius, 0);
    }

    /**
    * Cree un nouveau masque de convolution.
    * C'est un carre de cote (2 * radius + 1).
    * Tous les elements sont a 'valeurParDefaut'.
    * @param radius               Radius du masque de convolution.
    * @param valeurParDefaut     Valeur a mettre dans chaque element.
    */
    public Mask(int radius, double valeurParDefaut) {
        if (radius < 1) {
            throw new IllegalArgumentException("Le radius doit etre >= 1");
        }

        this.radius = radius;
        width = 2 * radius + 1;
        contenu = new double[width * width];

        for (int i = 0; i < width * width; i++) {
            contenu[i] = valeurParDefaut;
        }
    }

    /**
    * Cree un nouveau masque de convolution.
    * C'est un carre de cote (2 * radius + 1).
    * Les eléments sont initialisés avec les valeurs du tableau
    * @param radius               Radius du masque de convolution.
    * @param values              Valeurs du masque
    */
    public Mask(int radius, double[] valeurs) {
        if (radius < 1) {
            throw new IllegalArgumentException("Le radius doit etre >= 1");
        }

        this.radius = radius;
        width = 2 * radius + 1;
        contenu = new double[width * width];

        for (int i = 0; i < width * width; i++) {
            contenu[i] = valeurs[i];
        }
    }

    /**
    * Renvoie le radius (demie-width) du masque.
    * @return     Le radius.
    */
    public int getRadius() {
        return radius;
    }

    /**
    * Renvoie la width du masque (cote du carre).
    * @return     La width.
    */
    public int getWidth() {
        return width;
    }

    /**
    * Renvoie un element du masque.
    * @param x     Abscisse de l'element.
    * @param y     Ordonnee de l'element.
    * @return      La valeur contenue dans l'element de coordonnees (x,y).
    */
    public double get(int x, int y) {
        return contenu[ (y + radius) * width + x + radius];
    }

    /**
    * Modifie un element du masque.
    * @param x          Abscisse de l'element.
    * @param y          Ordonnee de l'element.
    * @param valeur     Valeur a inscrire dans l'element de coordonnees (x,y).
    */
    public void put(int x, int y, double valeur) {
        contenu[(y + radius) * width + x + radius] = valeur;
    }

}
