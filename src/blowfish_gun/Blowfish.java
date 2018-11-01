/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfish_gun;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;
import java.util.Base64;
import java.util.List;

/**
 *
 * @author Anggun Dwiguna
 */
public final class Blowfish {
    public long[] P = new Subkey().pbox;
    public long[][] S = new Subkey().SBox;
    public long datal = 0, datar = 0;
    List<String> data = null;

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();   
    String input;
    long L0 = 0, R0 = 0; //insialisasi string all-zero    
    
    public Blowfish(String inp, String kunci){
        //inisialisasi kunci
        byte[] keyByte = kunci.getBytes();
        
        System.out.println("\nALGORITMA BLOWFISH");
        for (int i=0 ; i<18 ; ++i){                     //inisialisasi p[0] sampai p[17], dimana index ganjil XOR dengan 32bit kunci blok kiri
            P[i] ^= keyByte[i % kunci.length()];        //dan index genap XOR dengan 32bit kunci blok kanan
        //  System.out.println("P["+i+"] = "+ P[i]);
        }

        for (int i=0 ; i<18 ; i+=2) {       //lakukan enkripsi string all-zero dengan p-array yg telah dibuat sebelumnya
           encrypt(datal, datar);                //enkripsi blok L dan blok R
           P[i] = datal; P[i+1] = datar;          //output blok kiri = p[index ganjil] dan output blok kanan = p[index genap]
//           System.out.println("P["+i+"] = "+ P[i]);
        }
//        System.out.println("Berhasil inisialisasi P-array\n");

        for (int i=0 ; i<4 ; ++i){           //menyiapkan variabel looping untuk 4 SBOX dengan 256 entri 32-bit
           for (int j=0 ; j<256; j+=2) { 
              encrypt(datal, datar);               //enkripsi blok L dan R
              S[i][j] = datal; S[i][j+1] = datar;   //simpan pada SBOX[i] entry ke [j] untuk blok L dan [j+1] untuk R
           }
//           System.out.println("Berhasil update data s-box ke " + i + "\n");
        }
        input = inp;        
        System.out.println("Input = " + inp + " Using key = " + kunci);
    }
    
    public static List<String> splitEqually(String text, int size) {
        // Give the list the right capacity to start with. You could use an array
        // instead if you wanted.
        List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);
        for (int start = 0; start < text.length(); start += size) {
            ret.add(text.substring(start, Math.min(text.length(), start + size)));
        }
        return ret;
    }
    
    public String getEncrypt(){
        data = splitEqually(input, 8);
        String baseEnc = null;
        byte[] ret = null, encrypted = null;                     
        
        for(String loop: data){
            loop = String.format("%-8s", loop).replace(' ', '*');
            
            //membagi plaintext menjadi 2 buah 32-bit data
            datal = StrtoIntHex(loop.substring(0, 4));
            datar = StrtoIntHex(loop.substring(4, 8));
            System.out.println(datal + " & " + datar);
            encrypt(datal, datar);

            encrypted = longtobyte(datal, datar);
            for(byte b: encrypted){
                System.out.print(b + " ");
            }
            try{
                outputStream.write(encrypted);
            }
            catch(IOException e){
                JOptionPane.showMessageDialog(null, e.getMessage());
            }            
        }
        encrypted = outputStream.toByteArray();
        baseEnc = Base64.getEncoder().encodeToString(encrypted);
        System.out.println("\n" + baseEnc);
        return baseEnc;
    }
    
    //melakukan dekripsi
    public String getDecrypt(){
        //decode string menjadi byte array
        byte[] baseDec = Base64.getDecoder().decode(input);        
        String decrypted = "";
        //mengubah ke dalam byte
        for(byte a: baseDec){
            System.out.print(a + " ");
        }System.out.println();
        
        
        
        for(int i = 0; i < baseDec.length; i+=8){
            byte[] splitter = Arrays.copyOfRange(baseDec, i, i+8);
            for(byte z: splitter){
                System.out.print(z + " ");
            }
            
            bytetolong(splitter);

            System.out.println(datal + " & " + datar);
            decrypt(datal, datar);
            decrypted += new String(longtobyte(datal, datar));  
        }
        System.out.println("Hasil dekripsi = " + decrypted);
        return decrypted;
    }
    
    public byte[] longtobyte(long dataleft, long dataright){
        byte[] ret = new byte[8];
        ret[0] = (byte)(dataleft >> 24);
        ret[1] = (byte)(dataleft >> 16 & 0xff);
        ret[2] = (byte)(dataleft >> 8 & 0xff);
        ret[3] = (byte)(dataleft & 0xff);
        ret[4] = (byte)(dataright >> 24);
        ret[5] = (byte)(dataright >> 16 & 0xff);
        ret[6] = (byte)(dataright >> 8 & 0xff);
        ret[7] = (byte)(dataright & 0xff);
        
        return ret;
    }
    
    public void bytetolong(byte[] param){
        datal = 0; datar = 0;
        for (int i = 0; i < 4; i++)
        {
            try{
                datal = (datal << 8) + (param[i] & 0xff);
                datar = (datar << 8) + (param[i+4] & 0xff);
            }
            catch(Exception e){
                JOptionPane.showMessageDialog(null, "Bytetolong gagal " + e.getMessage());
            }
        }
    }
    
    public String AppendChar(byte[] data){
        StringBuilder str = new StringBuilder();
        for(short b: data){
            str.append((char)b);
        }
        return str.toString();
    }

    public long f(long x){
        long h;        
        try{
            long S1 = S[0][(int)(x >> 24)];
            long S2 = S[1][(int)(x >> 16 & 0xff)];
            long S3 = S[2][(int)(x >> 8 & 0xff)];
            long S4 = S[3][(int)(x & 0xff)];            
            h = S1 & S2 ^ S3 & S4;            
//            System.out.println("Hasil dari h = " + h);
            return h;
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return 0;
    }
    
    public final void encrypt(long L, long R){
        for (int i=0 ; i<16 ; i += 2) {
            L ^= P[i];     // L = L XOR P[i]
            R ^= f(L);     // R = R XOR feistel(L)            
            R ^= P[i+1];   // R = R XOR P[i+1]
            L ^= f(R);     // L = L XOR feistel(R)
        }
        
        L ^= P[16];
        R ^= P[17];
        //lakukan swap untuk membatalkan last loop
        long temp = L;
        L = R; R = temp;
        datal = L;
        datar = R;
//        System.out.println("L = " + L + " R = " + R);
    }
    
    public void decrypt(long L, long R) {
        for (int i=16 ; i > 0 ; i -= 2) {
           L ^= P[i+1];
           R ^= f(L);
           R ^= P[i];
           L ^= f(R);
        }
        L ^= P[1];
        R ^= P[0];
        long temp = L;
        L = R; R = temp;
        datal = L;
        datar = R;
//        System.out.println("L = " + L + " R = " + R);
    }
    
    public int StrtoIntHex(String data){
        int ret;
        String hex="";
        for(byte b: data.getBytes()){
            hex+= Integer.toHexString(b);
        }
        return Integer.parseInt(hex, 16);
    }
}
