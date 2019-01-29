# blowfish-algorithm-gun
A repository to develop a simple blowfish algorithm to encrypt data such as text and other filetypes are coming soon.

### The Blowfish Encryption Algorithm
Blowfish is a symmetric block cipher that can be used as a drop-in replacement for DES or IDEA. It takes a variable-length key, from 32 bits to 448 bits, making it ideal for both domestic and exportable use. Blowfish was designed in 1993 by Bruce Schneier as a fast, free alternative to existing encryption algorithms. Since then it has been analyzed considerably, and it is slowly gaining acceptance as a strong encryption algorithm. Blowfish is unpatented and license-free, and is available free for all uses. ([Schneier](https://www.schneier.com/academic/blowfish/))

Quoted from [Schneier](https://www.schneier.com/academic/blowfish/)
> Everyone is welcome to download Blowfish and use it in their application. There are no rules about use, although I would appreciate being notified of any commercial applications using the product so that I can list them on this website.


### Pseudocode
Below you can find the pseudocode for Blowfish Algorithm. This code includes the encryption and decryption function which are the basic process in Blowfish. This pseudocode is referenced from [Wiki](https://en.wikipedia.org/wiki/Blowfish_(cipher)).

```
uint32_t P[18];
uint32_t S[4][256];

uint32_t f (uint32_t x) {
   uint32_t h = S[0][x >> 24] + S[1][x >> 16 & 0xff];
   return ( h ^ S[2][x >> 8 & 0xff] ) + S[3][x & 0xff];
}

void encrypt (uint32_t & L, uint32_t & R) {
   for (int i=0 ; i<16 ; i += 2) {
      L ^= P[i];
      R ^= f(L);
      R ^= P[i+1];
      L ^= f(R);
   }
   L ^= P[16];
   R ^= P[17];
   swap (L, R);
}

void decrypt (uint32_t & L, uint32_t & R) {
   for (int i=16 ; i > 0 ; i -= 2) {
      L ^= P[i+1];
      R ^= f(L);
      R ^= P[i];
      L ^= f(R);
   }
   L ^= P[1];
   R ^= P[0];
   swap (L, R);
}

  // ...
  // initializing the P-array and S-boxes with values derived from pi; omitted in the example
  // ...
{
   for (int i=0 ; i<18 ; ++i)
      P[i] ^= key[i % keylen];
   uint32_t L = 0, R = 0;
   for (int i=0 ; i<18 ; i+=2) {
      encrypt (L, R);
      P[i] = L; P[i+1] = R;
   }
   for (int i=0 ; i<4 ; ++i)
      for (int j=0 ; j<256; j+=2) {
         encrypt (L, R);
         S[i][j] = L; S[i][j+1] = R;
      }
}
```
