/*
 * Created on Jan 20, 2015
 *
 *
 * Author: Machel Higgins (machelhiggins@hotmail.com)
 */
package com.uwiseismic.kmeans;

public class ClusterableException extends Exception {

    public ClusterableException(String message){
        super(message);
    }

    public ClusterableException(String message, Throwable cause){
        super(message, cause);
    }
}
