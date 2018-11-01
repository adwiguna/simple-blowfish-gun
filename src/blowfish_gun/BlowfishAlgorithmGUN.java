/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfish_gun;

/**
 *
 * @author Anggun Dwiguna
 */
public class BlowfishAlgorithmGUN {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        String inp="anggun";
        String kunci="dwiguna";
        System.out.println("BLOWFISH ALGORITHM BY GUN\n=================================");
        System.out.println("The result of the encryption using this algorithm will be presented in Base64 encoded string");

        Blowfish bfish = new Blowfish(inp, kunci);
        bfish.getEncrypt();
    }
    
}
