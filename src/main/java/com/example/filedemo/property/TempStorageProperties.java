/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.filedemo.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author appcino
 */
@ConfigurationProperties(prefix = "file")
public class TempStorageProperties {
    private String tempDir;

    public String getTempDir() {
        return tempDir;
    }

    public void setTempDir(String tempDir) {
        this.tempDir = tempDir;
    }
    
    
    
}
