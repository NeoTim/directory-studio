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
package org.apache.directory.studio.openldap.config.editor.pages;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.common.ui.CommonUIUtils;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.common.ui.widgets.TableWidget;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyEvent;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyListener;
import org.apache.directory.studio.openldap.common.ui.model.AllowFeatureEnum;
import org.apache.directory.studio.openldap.common.ui.model.AuthzPolicyEnum;
import org.apache.directory.studio.openldap.common.ui.model.DisallowFeatureEnum;
import org.apache.directory.studio.openldap.common.ui.model.RequireConditionEnum;
import org.apache.directory.studio.openldap.common.ui.model.RestrictOperationEnum;
import org.apache.directory.studio.openldap.config.editor.OpenLDAPServerConfigurationEditor;
import org.apache.directory.studio.openldap.config.editor.wrappers.AllowFeatureDecorator;
import org.apache.directory.studio.openldap.config.editor.wrappers.AuthIdRewriteDecorator;
import org.apache.directory.studio.openldap.config.editor.wrappers.AuthIdRewriteWrapper;
import org.apache.directory.studio.openldap.config.editor.wrappers.AuthzRegexpDecorator;
import org.apache.directory.studio.openldap.config.editor.wrappers.AuthzRegexpWrapper;
import org.apache.directory.studio.openldap.config.editor.wrappers.DisallowFeatureDecorator;
import org.apache.directory.studio.openldap.config.editor.wrappers.RequireConditionDecorator;
import org.apache.directory.studio.openldap.config.editor.wrappers.RestrictOperationDecorator;
import org.apache.directory.studio.openldap.config.editor.wrappers.StringValueDecorator;
import org.apache.directory.studio.openldap.config.editor.wrappers.StringValueWrapper;
import org.apache.directory.studio.openldap.config.model.OlcGlobal;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;


/**
 * This class represents the Options Page of the Server Configuration Editor. We manage the 
 * following parameters :
 * <ul>
 *   <li>Operations and features :
 *     <ul>
 *       <li>olcAllows</li>
 *       <li>olcDisallows</li>
 *       <li>olcRequires</li>
 *       <li>olcRestrict</li>
 *     </ul>
 *   </li>
 *   <li>Authorization regexp & rewrite rules :
 *     <ul>
 *       <li>olcAuthIDRewrite</li>
 *       <li>olcAuthzRegexp</li>
 *     </ul>
 *   </li>
 *   <li>Miscellaneous options :
 *     <ul>
 *       <li>olcArgsFile</li>
 *       <li>olcPluginLogFile</li>
 *       <li>olcReferral</li>
 *       <li>olcAuthzPolicy</li>
 *       <li>olcRootDSE</li>
 *       <li>olcReadOnly</li>
 *       <li>olcGentleHUP</li>
 *       <li>olcReadOnly</li>
 *       <li>olcReverseLookup</li>
 *     </ul>
 *   </li>
 * </ul>
 * Here is the content of this page :
 * <pre>
 * .--------------------------------------------------------------------------------------.
 * | Options                                                                              |
 * +--------------------------------------------------------------------------------------+
 * | .---------------------------------------. .----------------------------------------. |
 * | |V Features                             | |V Operations                            | |
 * | +---------------------------------------+ +----------------------------------------+ |
 * | |                                       | |                                        | |
 * | | Allowed Features :                    | | Required Conditions :                  | |
 * | | +--------------------------+          | | +--------------------------+           | |
 * | | | xyz                      | (Add...) | | | xyz                      | (Add...)  | |
 * | | | abcde                    |          | | | abcde                    |           | |
 * | | | aaa                      | (Delete) | | | aaa                      | (Delete)  | |
 * | | +--------------------------+          | | +--------------------------+           | |
 * | |                                       | |                                        | |
 * | | Disallowed Features :                 | | Restricted Operations :                | |
 * | | +--------------------------+          | | +--------------------------+           | |
 * | | | xyz                      | (Add...) | | | xyz                      | (Add...)  | |
 * | | | abcde                    |          | | | abcde                    |           | |
 * | | | aaa                      | (Delete) | | | aaa                      | (Delete)  | |
 * | | +--------------------------+          | | +--------------------------+           | |
 * | +---------------------------------------+ +----------------------------------------+ |
 * | .---------------------------------------. .----------------------------------------. |
 * | |V Authorization rewrite rules          | |V Authorisation ID Regexp               | |
 * | +---------------------------------------+ +----------------------------------------+ |
 * | | +--------------------------+          | | +--------------------------+           | |
 * | | | xyz                      | (Add...) | | | xyz                      | (Add...)  | |
 * | | | abcde                    | (Edit)   | | | abcde                    | (Edit...) | |
 * | | | aaa                      | (Delete) | | | aaa                      | (Delete)  | |
 * | | |                          | -------- | | |                          | --------- | |
 * | | |                          | (Up...)  | | |                          | (Up...)   | |
 * | | +--------------------------+ (Down)   | | +--------------------------+ (Down...) | |
 * | +---------------------------------------+ +----------------------------------------+ |
 * | .----------------------------------------------------------------------------------. |
 * | |V Miscellaneous options                                                           | |
 * | +----------------------------------------------------------------------------------+ |
 * | | Args File : [///////////////////////]     Plugin Log File : [//////////////////] | |
 * | |                                                                                  | |
 * | | Referral :  [///////////////////////]     Authz Policy :    [------------------] | |
 * | |                                                                                  | |
 * | | Root DSE :                                Other :                                | |
 * | | +--------------------------+              .------------------------------------. | |
 * | | | xyz                      | (Add...)     | [X] Read Only                      | | |
 * | | | abcde                    |              | [X] GentleHUP                      | | |
 * | | | aaa                      | (Delete)     | [X] Reverse Lookup                 | | |
 * | | +--------------------------+              '------------------------------------' | |
 * | +----------------------------------------------------------------------------------+ |
 * +--------------------------------------------------------------------------------------+
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OptionsPage extends OpenLDAPServerConfigurationEditorPage
{
    /** The Page ID*/
    public static final String ID = OptionsPage.class.getName(); //$NON-NLS-1$

    /** The Page Title */
    private static final String TITLE = Messages.getString( "OpenLDAPOptionsPage.Title" );

    // UI Controls
    // Operations and features
    /** The olcAllows parameter */
    private TableWidget<AllowFeatureEnum> allowFeatureTableWidget;

    /** The olcDisallows parameter */
    private TableWidget<DisallowFeatureEnum> disallowFeatureTableWidget;
    
    /** The olcRequires parameter */
    private TableWidget<RequireConditionEnum> requireConditionTableWidget;

    /** The olcRestrict parameter */
    private TableWidget<RestrictOperationEnum> restrictOperationTableWidget;

    // The Authz regexp and rewrite rules
    /** The olcAuthIDRewrite parameter */
    private TableWidget<AuthIdRewriteWrapper> authIdRewriteTableWidget;

    /** The olcAuthzRegexp parameter */
    private TableWidget<AuthzRegexpWrapper> authzRegexpTableWidget;

    // The miscellaneous parameters
    /** The olcArgsFile parameter */
    private Text argsFileText; 

    /** The olcPluginLogFile parameter */
    private Text pluginLogFileText; 

    /** The olcReferral parameter */
    private Text referralText; 

    /** The olcRootDSE parameter */
    private TableWidget<StringValueWrapper> rootDseTableWidget; 

    /** The olcAuthzPolicy parameter */
    private Combo authzPolicyCombo; 

    /** The olcGentleHUP parameter */
    private Button gentleHupCheckbox;

    /** The olcReadOnly parameter */
    private Button readOnlyCheckbox;

    /** The olcReverseLookup parameter */
    private Button reverseLookupCheckbox;
    
    
    /**
     * The olcAllows listener
     */
    private WidgetModifyListener allowFeatureListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent e )
        {
            List<String> allowFeatures = new ArrayList<String>();
            
            for ( AllowFeatureEnum allowFeature : allowFeatureTableWidget.getElements() )
            {
                allowFeatures.add( allowFeature.getName() );
            }
            
            getConfiguration().getGlobal().setOlcAllows( allowFeatures );
        }
    };
    
    
    /**
     * The olcDisallows listener
     */
    private WidgetModifyListener disallowFeatureListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent e )
        {
            List<String> disallowFeatures = new ArrayList<String>();
            
            for ( DisallowFeatureEnum disallowFeature : disallowFeatureTableWidget.getElements() )
            {
                disallowFeatures.add( disallowFeature.getName() );
            }
            
            getConfiguration().getGlobal().setOlcDisallows( disallowFeatures );
        }
    };
    
    
    /**
     * The olcRequires listener
     */
    private WidgetModifyListener requireConditionListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent e )
        {
            List<String> requires = new ArrayList<String>();
            
            for ( RequireConditionEnum requireCondition : requireConditionTableWidget.getElements() )
            {
                requires.add( requireCondition.getName() );
            }
            
            getConfiguration().getGlobal().setOlcRequires( requires );
        }
    };
    
    
    /**
     * The olcRestrict listener
     */
    private WidgetModifyListener restrictOperationListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent e )
        {
            List<String> restricts = new ArrayList<String>();
            
            for ( RestrictOperationEnum restrictOperation : restrictOperationTableWidget.getElements() )
            {
                restricts.add( restrictOperation.getName() );
            }
            
            getConfiguration().getGlobal().setOlcRestrict( restricts );
        }
    };
    
    
    /**
     * The olcAuthIdRewrite listener
     */
    private WidgetModifyListener authIdRewriteListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent e )
        {
            List<String> authIdRewrites = new ArrayList<String>();
            
            for ( AuthIdRewriteWrapper authIdRewrite : authIdRewriteTableWidget.getElements() )
            {
                authIdRewrites.add( authIdRewrite.toString() );
            }
            
            getConfiguration().getGlobal().setOlcAuthIDRewrite( authIdRewrites );
        }
    };
    
    
    /**
     * The olcAuthzRegexp listener
     */
    private WidgetModifyListener authzRegexpListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent e )
        {
            List<String> authzRegexps = new ArrayList<String>();
            
            for ( AuthzRegexpWrapper authzRegexp : authzRegexpTableWidget.getElements() )
            {
                authzRegexps.add( authzRegexp.toString() );
            }
            
            getConfiguration().getGlobal().setOlcAuthzRegexp( authzRegexps );
        }
    };
    
    
    /**
     * The olcArgsFile listener
     */
    private ModifyListener argsFileTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getConfiguration().getGlobal().setOlcArgsFile( argsFileText.getText() );
        }
    };
    
    
    /**
     * The olcPluginFileLog listener
     */
    private ModifyListener pluginLogFileTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getConfiguration().getGlobal().setOlcPluginLogFile( pluginLogFileText.getText() );
        }
    };
    
    
    /**
     * The olcReferral listener
     */
    private ModifyListener referralTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            getConfiguration().getGlobal().setOlcReferral( referralText.getText() );
        }
    };
    
    
    /**
     * The olcRootDSE listener
     */
    private WidgetModifyListener rootDseTableListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent e )
        {
            List<String> rootDses = new ArrayList<String>();
            
            for ( StringValueWrapper rootDse : rootDseTableWidget.getElements() )
            {
                rootDses.add( rootDse.getValue() );
            }
            
            getConfiguration().getGlobal().setOlcRootDSE( rootDses );
        }
    };
    
    
    /**
     * The olcAuthzPolicy listener
     */
    private SelectionListener authzPolicyComboListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            getConfiguration().getGlobal().setOlcAuthzPolicy( authzPolicyCombo.getText() );
        }
    };

    
    /**
     * The olcGentleHup listener
     */
    private SelectionListener gentleHupCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            getConfiguration().getGlobal().setOlcGentleHUP( gentleHupCheckbox.getSelection() );
        }
    };

    
    /**
     * The olcReadOnly listener
     */
    private SelectionListener readOnlyCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            getConfiguration().getGlobal().setOlcReadOnly( readOnlyCheckbox.getSelection() );
        }
    };

    
    /**
     * The olcReverseLookup listener
     */
    private SelectionListener reverseLookupCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            getConfiguration().getGlobal().setOlcReverseLookup( reverseLookupCheckbox.getSelection() );
        }
    };

    
    /**
     * Creates a new instance of OptionsPage.
     *
     * @param editor the associated editor
     */
    public OptionsPage( OpenLDAPServerConfigurationEditor editor )
    {
        super( editor, ID, TITLE );
    }


    /**
     * {@inheritDoc}
     * Creates the OpenLDAP options config Tab. It contains 5 sections, in "
     * columns and 2 rows
     * 
     * <pre>
     * +--------------+---------------+
     * |              |               |
     * |   Features   |  Operations   |
     * |              |               |
     * +--------------+---------------+
     * |              |               |
     * |    rewrite   |    regexp     |
     * |              |               |
     * +--------------+---------------+
     * |                              |
     * |        Miscellaneous         |
     * |                              |
     * +------------------------------+
     */
    protected void createFormContent( Composite parent, FormToolkit toolkit )
    {
        TableWrapLayout twl = new TableWrapLayout();
        twl.numColumns = 2;
        parent.setLayout( twl );

        // The upper left part
        Composite uperLeftComposite = toolkit.createComposite( parent );
        uperLeftComposite.setLayout( new GridLayout() );
        TableWrapData upperLeftCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP, 1, 1 );
        upperLeftCompositeTableWrapData.grabHorizontal = true;
        uperLeftComposite.setLayoutData( upperLeftCompositeTableWrapData );

        // The upper right part
        Composite upperRightComposite = toolkit.createComposite( parent );
        upperRightComposite.setLayout( new GridLayout() );
        TableWrapData upperRightCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP, 1, 1 );
        upperRightCompositeTableWrapData.grabHorizontal = true;
        upperRightComposite.setLayoutData( upperRightCompositeTableWrapData );

        // The middle left part
        Composite middleLeftComposite = toolkit.createComposite( parent );
        middleLeftComposite.setLayout( new GridLayout() );
        TableWrapData middleLeftCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP, 1, 1 );
        middleLeftCompositeTableWrapData.grabHorizontal = true;
        middleLeftComposite.setLayoutData( middleLeftCompositeTableWrapData );

        // The middle right part
        Composite middleRightComposite = toolkit.createComposite( parent );
        middleRightComposite.setLayout( new GridLayout() );
        TableWrapData middleRightCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP, 1, 1 );
        middleRightCompositeTableWrapData.grabHorizontal = true;
        middleRightComposite.setLayoutData( middleRightCompositeTableWrapData );

        // The lower part
        Composite lowerComposite = toolkit.createComposite( parent );
        lowerComposite.setLayout( new GridLayout() );
        TableWrapData lowerCompositeTableWrapData = new TableWrapData( TableWrapData.FILL, TableWrapData.TOP, 1, 2 );
        lowerCompositeTableWrapData.grabHorizontal = true;
        lowerComposite.setLayoutData( lowerCompositeTableWrapData );

        createFeaturesSection( toolkit, middleLeftComposite );
        createOperationsSection( toolkit, middleRightComposite );
        createAuthorizationRewriteRuleSection( toolkit, middleLeftComposite );
        createAuthIdRegexpsSection( toolkit, middleRightComposite );
        createMiscellaneousSection( toolkit, lowerComposite );
    }


    /**
     * Creates the Features section.
     *
     *<pre>
     * .---------------------------------------.
     * |V Features                             |
     * +---------------------------------------+
     * |                                       |
     * | Allowed Features :                    |
     * | +--------------------------+          |
     * | | xyz                      | (Add...) |
     * | | abcde                    |          |
     * | | aaa                      | (Delete) |
     * | +--------------------------+          |
     * |                                       |
     * | Disallowed Features :                 |
     * | +--------------------------+          |
     * | | xyz                      | (Add...) |
     * | | abcde                    |          |
     * | | aaa                      | (Delete) |
     * | +--------------------------+          |
     * +---------------------------------------+
     *</pre>
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createFeaturesSection( FormToolkit toolkit, Composite parent )
    {
        // The Features section, which can be expanded or compacted
        Section section = createSection( toolkit, parent, 
            Messages.getString( "OpenLDAPOptionsPage.FeaturesSection" ) );
        Composite composite = createSectionComposite( toolkit, section, 2, false );

        // The olcAllows parameter label
        Label allowFeatureLabel = toolkit.createLabel( composite, 
            Messages.getString( "OpenLDAPOptionsPage.AllowFeature" ) ); //$NON-NLS-1$
        allowFeatureLabel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false, 2, 1 ) );

        // The olcAllows parameter table
        allowFeatureTableWidget = new TableWidget<AllowFeatureEnum>( new AllowFeatureDecorator( composite.getShell() ) );

        allowFeatureTableWidget.createWidgetNoEdit( composite, toolkit );
        allowFeatureTableWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        addModifyListener( allowFeatureTableWidget, allowFeatureListener );

        // The olcDisallows parameter label
        Label disallowFeatureLabel = toolkit.createLabel( composite,
            Messages.getString( "OpenLDAPOptionsPage.DisallowFeature" ) ); //$NON-NLS-1$
        disallowFeatureLabel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false, 2, 1 ) );

        // The olcDisallows parameter table
        disallowFeatureTableWidget = new TableWidget<DisallowFeatureEnum>( new DisallowFeatureDecorator( composite.getShell() ) );

        disallowFeatureTableWidget.createWidgetNoEdit( composite, toolkit );
        disallowFeatureTableWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        addModifyListener( disallowFeatureTableWidget, disallowFeatureListener );
    }


    /**
     * Creates the Operations section.
     * <pre>
     * .----------------------------------------.
     * |V Operations                            |
     * +----------------------------------------+
     * |                                        |
     * | Required Conditions :                  |
     * | +--------------------------+           |
     * | | xyz                      | (Add...)  |
     * | | abcde                    |           |
     * | | aaa                      | (Delete)  |
     * | +--------------------------+           |
     * |                                        |
     * | Restricted Operations :                |
     * | +--------------------------+           |
     * | | xyz                      | (Add...)  |
     * | | abcde                    |           |
     * | | aaa                      | (Delete)  |
     * | +--------------------------+           |
     * +----------------------------------------+
     * </pre>
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createOperationsSection( FormToolkit toolkit, Composite parent )
    {
        // The Operations section, which can be expanded or compacted
        Section section = createSection( toolkit, parent, 
            Messages.getString( "OpenLDAPOptionsPage.OperationsSection" ) );
        Composite composite = createSectionComposite( toolkit, section, 2, false );

        // The olcRequires parameter label
        Label requireConditionLabel = toolkit.createLabel( composite, 
            Messages.getString( "OpenLDAPOptionsPage.RequireCondition" ) ); //$NON-NLS-1$
        requireConditionLabel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false, 2, 1 ) );

        // The olcRequires parameter table
        requireConditionTableWidget = new TableWidget<RequireConditionEnum>( new RequireConditionDecorator( composite.getShell() ) );

        requireConditionTableWidget.createWidgetNoEdit( composite, toolkit );
        requireConditionTableWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        addModifyListener( requireConditionTableWidget, requireConditionListener );

        // The olcRestrict parameter label
        Label restrictOperationLabel = toolkit.createLabel( composite,
            Messages.getString( "OpenLDAPOptionsPage.RestrictOperation" ) ); //$NON-NLS-1$
        restrictOperationLabel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false, 2, 1 ) );

        // The olcRestrict parameter table
        restrictOperationTableWidget = new TableWidget<RestrictOperationEnum>( new RestrictOperationDecorator( composite.getShell() ) );

        restrictOperationTableWidget.createWidgetNoEdit( composite, toolkit );
        restrictOperationTableWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        addModifyListener( restrictOperationTableWidget, restrictOperationListener );
    }


    /**
     * Creates the Authorization Rewrite Rules section.
     * <pre>
     * .----------------------------------------.
     * |V Authorization rewrite rules           |
     * +----------------------------------------+
     * | +--------------------------+           |
     * | | xyz                      | (Add...)  |
     * | | abcde                    | (Edit...) |
     * | | aaa                      | (Delete)  |
     * | |                          | --------- |
     * | |                          | (Up...)   |
     * | +--------------------------+ (Down...) |
     * +----------------------------------------+
     * </pre>
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createAuthorizationRewriteRuleSection( FormToolkit toolkit, Composite parent )
    {
        Section section = createSection( toolkit, parent, 
            Messages.getString( "OpenLDAPOptionsPage.AuthzRewriteRules" ) );
        Composite composite = createSectionComposite( toolkit, section, 2, false );

        // The olcAuthIdRewrite parameter table
        authIdRewriteTableWidget = new TableWidget<AuthIdRewriteWrapper>( 
            new AuthIdRewriteDecorator( composite.getShell() ) );

        authIdRewriteTableWidget.createWidgetNoEdit( composite, toolkit );
        authIdRewriteTableWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        addModifyListener( authIdRewriteTableWidget, authIdRewriteListener );
    }


    /**
     * Creates the AuthID Regexp section.
     * <pre>
     * .----------------------------------------.
     * |V Authentication ID Regexp              |
     * +----------------------------------------+
     * | +--------------------------+           | 
     * | | xyz                      | (Add...)  |
     * | | abcde                    | (Edit...) |
     * | | aaa                      | (Delete)  |
     * | |                          | --------- |
     * | |                          | (Up...)   |
     * | +--------------------------+ (Down...) |
     * +----------------------------------------+
     * </pre>
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createAuthIdRegexpsSection( FormToolkit toolkit, Composite parent )
    {
        Section section = createSection( toolkit, parent, 
            Messages.getString( "OpenLDAPOptionsPage.AuthIdRegexps" ) );
        Composite composite = createSectionComposite( toolkit, section, 2, false );

        // The olcAuthzRegexp parameter table
        authzRegexpTableWidget = new TableWidget<AuthzRegexpWrapper>( 
            new AuthzRegexpDecorator( composite.getShell() ) );

        authzRegexpTableWidget.createWidgetNoEdit( composite, toolkit );
        authzRegexpTableWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        addModifyListener( authzRegexpTableWidget, authzRegexpListener );
    }


    /**
     * Creates the miscellaneous section.
     * <pre>
     * .----------------------------------------------------------------------------------.
     * |V Miscellaneous options                                                           |
     * +----------------------------------------------------------------------------------+
     * | Args File : [///////////////////////]     Plugin Log File : [//////////////////] |
     * |                                                                                  |
     * | Referral :  [///////////////////////]     Authz Policy :    [------------------] |
     * |                                                                                  |
     * | Root DSE :                                Other :                                |
     * | +--------------------------+              .------------------------------------. |
     * | | xyz                      | (Add...)     | [X] Read Only                      | |
     * | | abcde                    |              | [X] GentleHUP                      | |
     * | | aaa                      | (Delete)     | [X] Reverse Lookup                 | |
     * | +--------------------------+              '------------------------------------' |
     * +----------------------------------------------------------------------------------+
     * </pre>
     * @param toolkit the toolkit
     * @param parent the parent composite
     */
    private void createMiscellaneousSection( FormToolkit toolkit, Composite parent )
    {
        Section section = createSection( toolkit, parent, 
            Messages.getString( "OpenLDAPOptionsPage.Miscellaneous" ) );
        Composite composite = createSectionComposite( toolkit, section, 5, false );

        // The olcArgFile parameter.
        argsFileText = CommonUIUtils.createText( toolkit, composite, 
            Messages.getString( "OpenLDAPOptionsPage.ArgsFile" ), "", -1, argsFileTextListener );
        toolkit.createLabel( composite, "" );

        // The olcPluginLogFile parameter.
        pluginLogFileText = CommonUIUtils.createText( toolkit, composite, 
            Messages.getString( "OpenLDAPOptionsPage.PluginLogFile" ), "", -1, pluginLogFileTextListener );

        // The olcReferral parameter.
        referralText = CommonUIUtils.createText( toolkit, composite, 
            Messages.getString( "OpenLDAPOptionsPage.Referral" ), "", -1, referralTextListener );
        toolkit.createLabel( composite, "" );

        // The authzPolicy parameter
        toolkit.createLabel( composite, Messages.getString( "OpenLDAPSecurityPage.AuthzPolicy" ) ); //$NON-NLS-1$
        authzPolicyCombo = BaseWidgetUtils.createCombo( composite, AuthzPolicyEnum.getNames(), -1, 1 );
        authzPolicyCombo.addSelectionListener( authzPolicyComboListener );

        // The olcRootDSE label.
        toolkit.createLabel( composite, Messages.getString( "OpenLDAPOptionsPage.RootDSEs" ) );
        toolkit.createLabel( composite, "" );
        toolkit.createLabel( composite, "" );

        // The olcOther label.
        toolkit.createLabel( composite, 
            Messages.getString( "OpenLDAPOptionsPage.Others" ) );
        toolkit.createLabel( composite, "" );

        // The olcRootDSE parameter.
        rootDseTableWidget = new TableWidget<StringValueWrapper>( 
            new StringValueDecorator( composite.getShell(), Messages.getString( "OpenLDAPOptionsPage.RootDSE" ) ) );

        rootDseTableWidget.createWidgetWithEdit( composite, toolkit );
        rootDseTableWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        addModifyListener( rootDseTableWidget, rootDseTableListener );
        toolkit.createLabel( composite, "" );

        // A group for the others checkboxes
        Group othersGroup = BaseWidgetUtils.createGroup( composite, null, 2 );
        GridLayout othersGroupGridLayout = new GridLayout( 3, false );
        othersGroup.setLayout( othersGroupGridLayout );
        othersGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        //toolkit.createLabel( composite, "" );

        // The olcGentleHUP Button
        gentleHupCheckbox = BaseWidgetUtils.createCheckbox( othersGroup, 
            Messages.getString( "OpenLDAPOptionsPage.GentleHUP" ), 1 );
        gentleHupCheckbox.addSelectionListener( gentleHupCheckboxSelectionListener );

        // The olcReadOnly Button
        readOnlyCheckbox = BaseWidgetUtils.createCheckbox( othersGroup, 
            Messages.getString( "OpenLDAPOptionsPage.ReadOnly" ), 1 );
        readOnlyCheckbox.addSelectionListener( readOnlyCheckboxSelectionListener );
        toolkit.createLabel( composite, "" );

        // The olcReverseLookup Button
        reverseLookupCheckbox = BaseWidgetUtils.createCheckbox( othersGroup, 
            Messages.getString( "OpenLDAPOptionsPage.ReverseLookup" ), 1 );
        reverseLookupCheckbox.addSelectionListener( reverseLookupCheckboxSelectionListener );
    }

    
    /**
     * Adds listeners to UI Controls.
     */
    private void addListeners()
    {
        addDirtyListener( allowFeatureTableWidget );
        addDirtyListener( disallowFeatureTableWidget );
        addDirtyListener( requireConditionTableWidget );
        addDirtyListener( restrictOperationTableWidget );
        addDirtyListener( authIdRewriteTableWidget );
        addDirtyListener( authzRegexpTableWidget );
        addDirtyListener( argsFileText );
        addDirtyListener( pluginLogFileText );
        addDirtyListener( referralText );
        addDirtyListener( authzPolicyCombo );
        addDirtyListener( rootDseTableWidget );
        addDirtyListener( gentleHupCheckbox );
        addDirtyListener( readOnlyCheckbox );
        addDirtyListener( reverseLookupCheckbox );
    }

    
    /**
     * Removes listeners to UI Controls.
     */
    private void removeListeners()
    {
        removeDirtyListener( allowFeatureTableWidget );
        removeDirtyListener( disallowFeatureTableWidget );
        removeDirtyListener( requireConditionTableWidget );
        removeDirtyListener( restrictOperationTableWidget );
        removeDirtyListener( authIdRewriteTableWidget );
        removeDirtyListener( authzRegexpTableWidget );
        removeDirtyListener( argsFileText );
        removeDirtyListener( pluginLogFileText );
        removeDirtyListener( referralText );
        removeDirtyListener( authzPolicyCombo );
        removeDirtyListener( rootDseTableWidget );
        removeDirtyListener( gentleHupCheckbox );
        removeDirtyListener( readOnlyCheckbox );
        removeDirtyListener( reverseLookupCheckbox );
    }
    

    /**
     * {@inheritDoc}
     */
    public void refreshUI()
    {
        removeListeners();

        // Getting the global configuration object
        OlcGlobal global = getConfiguration().getGlobal();

        if ( global != null )
        {
            //
            // Assigning values to UI Controls
            //

            // Allow Feature Table Widget
            List<String> allowedFeatures = global.getOlcAllows();
            List<AllowFeatureEnum> alloweds = new ArrayList<AllowFeatureEnum>();

            if ( allowedFeatures != null )
            {
                for ( String allowedFeature : allowedFeatures )
                {
                    alloweds.add( AllowFeatureEnum.getFeature( allowedFeature ) );
                }
            }

            allowFeatureTableWidget.setElements( alloweds );

            // Disallow Feature Table Widget
            List<String> disallowedFeatures = global.getOlcDisallows();
            List<DisallowFeatureEnum> disalloweds = new ArrayList<DisallowFeatureEnum>();

            if ( disallowedFeatures != null )
            {
                for ( String disallowedFeature : disallowedFeatures )
                {
                    disalloweds.add( DisallowFeatureEnum.getFeature( disallowedFeature ) );
                }
            }
            
            disallowFeatureTableWidget.setElements( disalloweds );

            // Require Condition Table Widget
            List<String> requireConditions = global.getOlcRequires();
            List<RequireConditionEnum> requires = new ArrayList<RequireConditionEnum>();

            if ( requireConditions != null )
            {
                for ( String requireCondition : requireConditions )
                {
                    requires.add( RequireConditionEnum.getCondition( requireCondition ) );
                }
            }
            
            requireConditionTableWidget.setElements( requires );

            // Restrict Operation Condition Table Widget
            List<String> restrictOperations = global.getOlcRestrict();
            List<RestrictOperationEnum> restricts = new ArrayList<RestrictOperationEnum>();

            if ( restrictOperations != null )
            {
                for ( String restrictOperation : restrictOperations )
                {
                    restricts.add( RestrictOperationEnum.getOperation( restrictOperation ) );
                }
            }
            
            restrictOperationTableWidget.setElements( restricts );

            // AuthID Rewrite Table Widget
            List<String> authIdRewrites = global.getOlcAuthIDRewrite();
            List<AuthIdRewriteWrapper> rewrites = new ArrayList<AuthIdRewriteWrapper>();

            if ( authIdRewrites != null )
            {
                for ( String rewrite : authIdRewrites )
                {
                    rewrites.add( new AuthIdRewriteWrapper( rewrite ) );
                }
            }
            
            authIdRewriteTableWidget.setElements( rewrites );

            // Authz Regexp Table Widget
            List<String> authzRegexps = global.getOlcRestrict();
            List<AuthzRegexpWrapper> regexps = new ArrayList<AuthzRegexpWrapper>();

            if ( authzRegexps != null )
            {
                for ( String regexp : authzRegexps )
                {
                    regexps.add( new AuthzRegexpWrapper( regexp ) );
                }
            }
            
            authzRegexpTableWidget.setElements( regexps );

            // Update the ArgsFileText
            BaseWidgetUtils.setValue( global.getOlcArgsFile(), argsFileText );

            // Update the PluginLogFileText
            BaseWidgetUtils.setValue( global.getOlcPluginLogFile(), pluginLogFileText );

            // Update the ReferralText
            BaseWidgetUtils.setValue( global.getOlcReferral(), referralText );

            // AuthzPolicyl Combo
            String authzPolicy = global.getOlcAuthzPolicy();

            if ( authzPolicy != null )
            {
                // Select the right one
                boolean found = false;
                
                for ( String authzPolicyStr : AuthzPolicyEnum.getNames() )
                {
                    if ( authzPolicyStr.equalsIgnoreCase( authzPolicy ) )
                    {
                        authzPolicyCombo.setText( authzPolicyStr );
                        found = true;
                        break;
                    }
                }
                
                if ( !found )
                {
                    authzPolicyCombo.setText( AuthzPolicyEnum.UNKNOWN.getName() );
                }
            }
            else
            {
                authzPolicyCombo.setText( AuthzPolicyEnum.UNKNOWN.getName() );
            }

            // Update the RootDSEText
            List<String> rootDses = global.getOlcRootDSE();
            List<StringValueWrapper> roots = new ArrayList<StringValueWrapper>();

            if ( rootDses != null )
            {
                for ( String rootDse : rootDses )
                {
                    roots.add( new StringValueWrapper( rootDse, true ) );
                }
            }
            
            rootDseTableWidget.setElements( roots );
            
            // Update the GentleHupCheckbox
            BaseWidgetUtils.setValue( global.getOlcGentleHUP(), gentleHupCheckbox );
            
            // Update the ReadOnlyCheckbox
            BaseWidgetUtils.setValue( global.getOlcReadOnly(), readOnlyCheckbox );
            
            // Update the GentleHupCheckbox
            BaseWidgetUtils.setValue( global.getOlcReverseLookup(), reverseLookupCheckbox );

            addListeners();
        }
    }
}
