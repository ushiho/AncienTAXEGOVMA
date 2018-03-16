/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.Serializable;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;

/**
 *
 * @author ushiho
 */
@Named(value = "adhesionController")
@SessionScoped
public class AdhesionController implements Serializable{

    /**
     * Creates a new instance of AdhesionController
     */
    public AdhesionController() {
    }
    
}
