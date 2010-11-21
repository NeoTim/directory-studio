/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */

package org.apache.directory.studio.schemaeditor.view.wrappers;


import java.util.Comparator;
import java.util.List;

import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;


/**
 * This class is used to compare and sort ascending two TreeNode.
 */
public class FirstNameSorter implements Comparator<TreeNode>
{
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare( TreeNode o1, TreeNode o2 )
    {
        List<String> o1Names = null;
        List<String> o2Names = null;

        if ( ( o1 instanceof AttributeTypeWrapper ) && ( o2 instanceof AttributeTypeWrapper ) )
        {
            AttributeTypeImpl at1 = ( ( AttributeTypeWrapper ) o1 ).getAttributeType();
            AttributeTypeImpl at2 = ( ( AttributeTypeWrapper ) o2 ).getAttributeType();

            o1Names = at1.getNames();
            o2Names = at2.getNames();
        }
        else if ( ( o1 instanceof ObjectClassWrapper ) && ( o2 instanceof ObjectClassWrapper ) )
        {
            ObjectClassImpl oc1 = ( ( ObjectClassWrapper ) o1 ).getObjectClass();
            ObjectClassImpl oc2 = ( ( ObjectClassWrapper ) o2 ).getObjectClass();

            o1Names = oc1.getNames();
            o2Names = oc2.getNames();
        }
        else if ( ( o1 instanceof AttributeTypeWrapper ) && ( o2 instanceof ObjectClassWrapper ) )
        {
            AttributeTypeImpl at = ( ( AttributeTypeWrapper ) o1 ).getAttributeType();
            ObjectClassImpl oc = ( ( ObjectClassWrapper ) o2 ).getObjectClass();

            o1Names = at.getNames();
            o2Names = oc.getNames();
        }
        else if ( ( o1 instanceof ObjectClassWrapper ) && ( o2 instanceof AttributeTypeWrapper ) )
        {
            ObjectClassImpl oc = ( ( ObjectClassWrapper ) o1 ).getObjectClass();
            AttributeTypeImpl at = ( ( AttributeTypeWrapper ) o2 ).getAttributeType();

            o1Names = oc.getNames();
            o2Names = at.getNames();
        }

        // Comparing the First Name
        if ( ( o1Names != null ) && ( o2Names != null ) )
        {
            if ( ( o1Names.size() > 0 ) && ( o2Names.size() > 0 ) )
            {
                return o1Names.get( 0 ).compareToIgnoreCase( o2Names.get( 0 ) );
            }
            else if ( ( o1Names.size() == 0 ) && ( o2Names.size() > 0 ) )
            {
                return "".compareToIgnoreCase( o2Names.get( 0 ) ); //$NON-NLS-1$
            }
            else if ( ( o1Names.size() > 0 ) && ( o2Names.size() == 0 ) )
            {
                return o1Names.get( 0 ).compareToIgnoreCase( "" ); //$NON-NLS-1$
            }
        }

        // Default
        return o1.toString().compareToIgnoreCase( o2.toString() );
    }
}