/*
 * Copyright (C) 2010-2011 by Chris Cormack
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package uk.org.onegch.netkernel.liquibase;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import liquibase.resource.ClassLoaderResourceAccessor;

import org.netkernel.layer0.nkf.INKFRequestContext;
import org.netkernel.layer0.nkf.NKFException;
import org.netkernel.layer0.representation.IReadableBinaryStreamRepresentation;

public class NetKernelResourceAccessor extends ClassLoaderResourceAccessor {
  private INKFRequestContext context;
  
  public NetKernelResourceAccessor(INKFRequestContext aContext, ClassLoader cl) {
    super(cl);
    this.context= aContext;
  }
  
  @Override
  public InputStream getResourceAsStream(String arg0) throws IOException {
    InputStream is= super.getResourceAsStream(arg0);
    try {
      if (is == null && context.exists(arg0)) {
        is= context.source(arg0, IReadableBinaryStreamRepresentation.class).getInputStream();
      }
    } catch (NKFException e) {
      throw new IOException(e);
    }
    return is;
  }
  
  @Override
  public Enumeration<URL> getResources(String arg0) throws IOException {
    Enumeration<URL> resources= super.getResources(arg0);
    
    return resources;
  }
  
  @Override
  public ClassLoader toClassLoader() {
    return super.toClassLoader();
  }
}
