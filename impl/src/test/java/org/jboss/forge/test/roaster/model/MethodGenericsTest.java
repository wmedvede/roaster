/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.test.roaster.model;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Pattern;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaInterface;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.forge.roaster.model.source.TypeVariableSource;
import org.junit.Assert;
import org.junit.Test;

public class MethodGenericsTest
{

   @Test
   public void addAndRemoveGenericType() throws ClassNotFoundException
   {
      JavaClassSource javaClass = Roaster.create(JavaClassSource.class);
      
      MethodSource<JavaClassSource> method = javaClass.addMethod();
      method.addTypeVariable().setName("T");
      
      Assert.assertTrue(method.toString().contains("<T>"));
      Assert.assertTrue(method.getTypeVariables().get(0).getBounds().isEmpty());
      method.removeTypeVariable("T");
      Assert.assertFalse(method.toString().contains("<T>"));
   }

   @Test
   public void addMultipleGenerics() throws ClassNotFoundException
   {
      JavaClassSource javaClass = Roaster.create(JavaClassSource.class);
      MethodSource<JavaClassSource> method = javaClass.addMethod();

      method.addTypeVariable().setName("I");
      method.addTypeVariable().setName("O");
      Assert.assertTrue(Pattern.compile("<I, *O>").matcher(method.toString()).find());
      method.removeTypeVariable("I");
      Assert.assertTrue(method.toString().contains("<O>"));
   }

   @Test
   public void getMethodGenerics() throws ClassNotFoundException
   {
      JavaClassSource javaClass = Roaster.create(JavaClassSource.class);
      MethodSource<JavaClassSource> method = javaClass.addMethod();

      method.addTypeVariable().setName("I");
      method.addTypeVariable().setName("O");
      List<TypeVariableSource<JavaClassSource>> typeVariables = method.getTypeVariables();
      Assert.assertNotNull(typeVariables);
      Assert.assertEquals(2, typeVariables.size());
      Assert.assertEquals("I", typeVariables.get(0).getName());
      Assert.assertTrue(typeVariables.get(0).getBounds().isEmpty());
      Assert.assertEquals("O", typeVariables.get(1).getName());
      Assert.assertTrue(typeVariables.get(1).getBounds().isEmpty());
   }

   @Test
   public void classTypeVariableBounds() throws ClassNotFoundException
   {
      JavaClassSource javaClass = Roaster.create(JavaClassSource.class);
      MethodSource<JavaClassSource> method = javaClass.addMethod();
      method.addTypeVariable().setName("T").setBounds(CharSequence.class);
      Assert.assertTrue(method.toString().contains("<T extends CharSequence>"));
      method.getTypeVariable("T").
               setBounds(CharSequence.class, Serializable.class);
      Assert.assertTrue(method.toString().contains("<T extends CharSequence & Serializable>"));
      method.getTypeVariable("T").removeBounds();
      Assert.assertTrue(method.toString().contains("<T>"));
   }

   @Test
   public void javaTypeTypeVariableBounds() throws ClassNotFoundException
   {
      JavaInterface<?> foo = Roaster.create(JavaInterfaceSource.class).setPackage("it.coopservice.test").setName("Foo");
      JavaClassSource javaClass = Roaster.create(JavaClassSource.class);
      MethodSource<JavaClassSource> method = javaClass.addMethod();
      method.addTypeVariable().setName("T").setBounds(foo);
      Assert.assertTrue(method.toString().contains("<T extends Foo>"));
      JavaInterface<?> bar = Roaster.create(JavaInterfaceSource.class).setPackage("it.coopservice.test").setName("Bar");
      method.getTypeVariable("T").setBounds(foo, bar);
      Assert.assertTrue(method.toString().contains("<T extends Foo & Bar>"));
      method.getTypeVariable("T").removeBounds();
      Assert.assertTrue(method.toString().contains("<T>"));
   }

   @Test
   public void stringTypeVariableBounds() throws ClassNotFoundException
   {
      JavaClassSource javaClass = Roaster.create(JavaClassSource.class);
      MethodSource<JavaClassSource> method = javaClass.addMethod();
      method.addTypeVariable().setName("T").setBounds("com.something.Foo");
      Assert.assertTrue(method.toString().contains("<T extends com.something.Foo>"));
      method.getTypeVariable("T").setBounds("com.something.Foo", "com.something.Bar<T>");
      Assert.assertTrue(method.toString().contains("<T extends com.something.Foo & com.something.Bar<T>>"));
      method.getTypeVariable("T").removeBounds();
      Assert.assertTrue(method.toString().contains("<T>"));
   }
   
}
