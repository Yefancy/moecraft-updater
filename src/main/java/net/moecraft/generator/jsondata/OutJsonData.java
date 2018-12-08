//--------------------------------------------------
// Class OutputJson
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.jsondata;

import net.moecraft.generator.meta.MetaResult;

import java.io.IOException;

@FunctionalInterface
public interface OutJsonData {
    String encode(String basePath, MetaResult result) throws IOException;
}