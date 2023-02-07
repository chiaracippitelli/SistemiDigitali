package com.example.provacippi;

import android.media.Image;

public class Risultato {
    String label;
    String descrizione; //dove va buttato
    String frase; //curiosità

    public Risultato(String label) {
       this.label = this.setLabel(label);
        this.setDescrizione();
        this.setFrase();
    }

    public String getLabel() {
        return label;
    }


    public String setLabel(String label) {
        String res= "";
        if(label.equals("Cardboard")){
            res= "Cartone";
        }else if(label.equals("Paper")){
            res ="Carta";
        }else if(label.equals("Metal")){
            res ="Metallo";
        }else if(label.equals("Plastic")){
            res ="Plastica";
        }else if(label.equals("Trash")){
            res="Indifferenziata";
        }else if(label.equals("Glass")){
            res ="Vetro";
        }
        return res;

    }


    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione() {
       if(this.getLabel().equals("Cartone")){
           this.descrizione= "Per riciclare questo elemento, utilizza il bidone della carta!";
       }else if(this.getLabel().equals("Carta")){
           this.descrizione= "Per riciclare questo elemento, utilizza il bidone della carta!";
       }else if(this.getLabel().equals("Metallo")){
           this.descrizione= "Per riciclare questo elemento, devi consultare le regole del tuo comune. Spesso è insieme alla plastica, ma potrebbe essere con il vetro.";
       }else if(this.getLabel().equals("Plastica")){
           this.descrizione= "Per riciclare questo elemento, utilizza il bidone della plastica!";
       }else if(this.getLabel().equals("Indifferenziata")){
           this.descrizione= "Per riciclare questo elemento, utilizza il bidone dell'indifferenziato. Attento: se il rifiuto è di natura organica, invece devi usare l'umido!";
       }else if(this.getLabel().equals("Vetro")){
           this.descrizione= "Per riciclare questo elemento, utilizza il bidone del vetro!";
       }
        this.descrizione = descrizione;
    }

    public String getFrase() {
        return frase;
    }

    public void setFrase() {
        if(this.getLabel().equals("Cartone")){
            this.frase= "Curiosità: Il 100% delle scatole per prodotti fragili e voluminosi sono in cartone riciclato.";
        }else if(this.getLabel().equals("Carta")){
            this.frase= "Curiosità: Per ogni tonnellata di prodotti cellulosici, avviati a riciclo, si realizza un taglio di ben 1.038 Kg di anidride carbonica. ";
        }else if(this.getLabel().equals("Metallo")){
            this.frase= "Curiosità: Con 800 lattine si costruisce una bicicletta completa di accessori, con 360 una bicicletta da corsa, con 37 una caffettiera, con 3 un paio di occhiali.\n ";
        }else if(this.getLabel().equals("Plastica")){
            this.frase= "Curiosità: lo sapevi che ogni 13 bottiglie di plastica riciclate si può creare una maglietta da calcio? ";
        }else if(this.getLabel().equals("Indifferenziata")){
            this.frase= "Curiosità: Lo smaltimento dei rifiuti non riciclabili costa a tutti noi 90 Euro/tonnellata.";
        }else if(this.getLabel().equals("Vetro")){
            this.frase= "Curiosità: lo sapevi che il vetro è l'unico materiale riciclabile al 100%?";
        }
        this.frase = frase;
    }
}
