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

package org.apache.directory.studio.connection.core;


import org.apache.directory.shared.ldap.util.LdapURL;
import org.apache.directory.studio.connection.core.ConnectionParameter.AuthenticationMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.NetworkProvider;
import org.apache.directory.studio.connection.core.event.ConnectionEventRegistry;
import org.apache.directory.studio.connection.core.io.ConnectionWrapper;
import org.apache.directory.studio.connection.core.io.api.DirectoryApiConnectionWrapper;
import org.apache.directory.studio.connection.core.io.jndi.JNDIConnectionWrapper;
import org.eclipse.core.runtime.IAdaptable;


/**
 * The Connection class is the central .
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class Connection implements ConnectionPropertyPageProvider, IAdaptable
{
    /**
     * Enum for alias dereferencing method.
     * 
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    public static enum AliasDereferencingMethod
    {
        /** Never. */
        NEVER(0),

        /** Always. */
        ALWAYS(1),

        /** Finding. */
        FINDING(2),

        /** Search. */
        SEARCH(3);

        private final int ordinal;


        private AliasDereferencingMethod( int ordinal )
        {
            this.ordinal = ordinal;
        }


        /**
         * Gets the ordinal.
         * 
         * @return the ordinal
         */
        public int getOrdinal()
        {
            return ordinal;
        }


        /**
         * Gets the AliasDereferencingMethod by ordinal.
         * 
         * @param ordinal the ordinal
         * 
         * @return the AliasDereferencingMethod
         */
        public static AliasDereferencingMethod getByOrdinal( int ordinal )
        {
            switch ( ordinal )
            {
                case 0:
                    return NEVER;
                case 1:
                    return ALWAYS;
                case 2:
                    return FINDING;
                case 3:
                    return SEARCH;
                default:
                    return null;
            }
        }
    }

    /**
     * Enum for referral handling method.
     * 
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    public static enum ReferralHandlingMethod
    {
        /** Ignore. */
        IGNORE(0),

        /** Follow automatically. */
        FOLLOW(1),

        /** Manage. */
        //MANAGE(2),

        /** Follow manually. */
        FOLLOW_MANUALLY(3);

        private final int ordinal;


        private ReferralHandlingMethod( int ordinal )
        {
            this.ordinal = ordinal;
        }


        /**
         * Gets the ordinal.
         * 
         * @return the ordinal
         */
        public int getOrdinal()
        {
            return ordinal;
        }


        /**
         * Gets the ReferralHandlingMethod by ordinal.
         * 
         * @param ordinal the ordinal
         * 
         * @return the ReferralHandlingMethod
         */
        public static ReferralHandlingMethod getByOrdinal( int ordinal )
        {
            switch ( ordinal )
            {
                case 0:
                    return IGNORE;
                case 1:
                    return FOLLOW;
                case 2:
                    //return MANAGE;
                    return FOLLOW_MANUALLY;
                case 3:
                    return FOLLOW_MANUALLY;
                default:
                    return null;
            }
        }
    }

    private ConnectionParameter connectionParameter;

    /** The connection wrapper */
    private ConnectionWrapper connectionWrapper;


    /**
     * Creates a new instance of Connection.
     *
     * @param connectionParameter
     */
    public Connection( ConnectionParameter connectionParameter )
    {
        this.connectionParameter = connectionParameter;
    }


    /**
     * @see java.lang.Object#clone()
     */
    public Object clone()
    {
        ConnectionParameter cp = new ConnectionParameter( getName(), getHost(), getPort(), getEncryptionMethod(),
            getNetworkProvider(), getAuthMethod(), getBindPrincipal(), getBindPassword(), getSaslRealm(), isReadOnly(),
            getConnectionParameter().getExtendedProperties() );

        Connection clone = new Connection( cp );

        return clone;
    }


    /**
     * Gets the connection wrapper.
     * 
     * @return the connection wrapper
     */
    public ConnectionWrapper getConnectionWrapper()
    {
        switch ( connectionParameter.getNetworkProvider() )
        {
            case JNDI:
                return getJndiConnectionWrapper();
            case APACHE_DIRECTORY_LDAP_API:
                return getDirectoryApiConnectionWrapper();
        }

        return null;
    }


    /**
     * Gets a JNDI connection wrapper.
     *
     * @return
     *      a JNDI connection wrapper
     */
    private JNDIConnectionWrapper getJndiConnectionWrapper()
    {
        if ( ( connectionWrapper == null ) || !( connectionWrapper instanceof JNDIConnectionWrapper ) )
        {
            connectionWrapper = new JNDIConnectionWrapper( this );
        }

        return ( JNDIConnectionWrapper ) connectionWrapper;
    }


    /**
     * Gets a Directory API connection wrapper.
     *
     * @return
     *      a Directory API connection wrapper
     */
    private DirectoryApiConnectionWrapper getDirectoryApiConnectionWrapper()
    {
        if ( ( connectionWrapper == null ) || !( connectionWrapper instanceof DirectoryApiConnectionWrapper ) )
        {
            connectionWrapper = new DirectoryApiConnectionWrapper( this );
        }

        return ( DirectoryApiConnectionWrapper ) connectionWrapper;
    }


    /**
     * Gets the connection parameter.
     * 
     * @return the connection parameter
     */
    public ConnectionParameter getConnectionParameter()
    {
        return connectionParameter;
    }


    /**
     * Sets the connection parameter.
     * 
     * @param connectionParameter the connection parameter
     */
    public void setConnectionParameter( ConnectionParameter connectionParameter )
    {
        this.connectionParameter = connectionParameter;
        ConnectionEventRegistry.fireConnectionUpdated( this, this );
    }


    /**
     * Gets the auth method.
     * 
     * @return the auth method
     */
    public AuthenticationMethod getAuthMethod()
    {
        return connectionParameter.getAuthMethod();
    }


    /**
     * Gets the bind password.
     * 
     * @return the bind password
     */
    public String getBindPassword()
    {
        return connectionParameter.getBindPassword();
    }


    /**
     * Gets the bind principal.
     * 
     * @return the bind principal
     */
    public String getBindPrincipal()
    {
        return connectionParameter.getBindPrincipal();
    }


    /**
     * Gets the encryption method.
     * 
     * @return the encryption method
     */
    public EncryptionMethod getEncryptionMethod()
    {
        return connectionParameter.getEncryptionMethod();
    }


    /**
     * Gets the network provider.
     * 
     * @return the network provider
     */
    public NetworkProvider getNetworkProvider()
    {
        return connectionParameter.getNetworkProvider();
    }


    /**
     * Gets the id.
     * 
     * @return the id
     */
    public String getId()
    {
        return connectionParameter.getId();
    }


    /**
     * Gets the host.
     * 
     * @return the host
     */
    public String getHost()
    {
        return connectionParameter.getHost();
    }


    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName()
    {
        return connectionParameter.getName();
    }


    /**
     * Gets the port.
     * 
     * @return the port
     */
    public int getPort()
    {
        return connectionParameter.getPort();
    }


    /**
     * Gets the SASL realm.
     * 
     * @return the SASL realm
     */
    public String getSaslRealm()
    {
        return connectionParameter.getSaslRealm();
    }


    /**
     * Checks if this connection is read only.
     * 
     * @return true, if this connection is read only
     */
    public boolean isReadOnly()
    {
        return connectionParameter.isReadOnly();
    }


    /**
     * Sets the auth method.
     * 
     * @param authMethod the auth method
     */
    public void setAuthMethod( AuthenticationMethod authMethod )
    {
        connectionParameter.setAuthMethod( authMethod );
        ConnectionEventRegistry.fireConnectionUpdated( this, this );
    }


    /**
     * Sets the bind password.
     * 
     * @param bindPassword the bind password
     */
    public void setBindPassword( String bindPassword )
    {
        connectionParameter.setBindPassword( bindPassword );
        ConnectionEventRegistry.fireConnectionUpdated( this, this );
    }


    /**
     * Sets the bind principal.
     * 
     * @param bindPrincipal the bind principal
     */
    public void setBindPrincipal( String bindPrincipal )
    {
        connectionParameter.setBindPrincipal( bindPrincipal );
        ConnectionEventRegistry.fireConnectionUpdated( this, this );
    }


    /**
     * Sets the encryption method.
     * 
     * @param encryptionMethod the encryption method
     */
    public void setEncryptionMethod( EncryptionMethod encryptionMethod )
    {
        connectionParameter.setEncryptionMethod( encryptionMethod );
        ConnectionEventRegistry.fireConnectionUpdated( this, this );
    }


    /**
     * Sets the host.
     * 
     * @param host the host
     */
    public void setHost( String host )
    {
        connectionParameter.setHost( host );
        ConnectionEventRegistry.fireConnectionUpdated( this, this );
    }


    /**
     * Sets the name.
     * 
     * @param name the name
     */
    public void setName( String name )
    {
        connectionParameter.setName( name );
        ConnectionEventRegistry.fireConnectionUpdated( this, this );
    }


    /**
     * Sets the port.
     * 
     * @param port the port
     */
    public void setPort( int port )
    {
        connectionParameter.setPort( port );
        ConnectionEventRegistry.fireConnectionUpdated( this, this );
    }


    /**
     * Sets the SASL realm.
     * 
     * @param realm the new SASL realm
     */
    public void setSaslRealm( String realm )
    {
        connectionParameter.setSaslRealm( realm );
        ConnectionEventRegistry.fireConnectionUpdated( this, this );
    }


    /**
     * Sets the read only flag.
     * 
     * @param isReadOnly the new read only flag
     */
    public void setReadOnly( boolean isReadOnly )
    {
        connectionParameter.setReadOnly( isReadOnly );
        ConnectionEventRegistry.fireConnectionUpdated( this, this );
    }


    /**
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public Object getAdapter( Class adapter )
    {
        //        if ( adapter.isAssignableFrom( ISearchPageScoreComputer.class ) )
        //        {
        //            return new LdapSearchPageScoreComputer();
        //        }
        if ( adapter == Connection.class )
        {
            return this;
        }

        return null;
    }


    /**
     * Gets the LDAP URL.
     * 
     * @return the LDAP URL
     */
    public LdapURL getUrl()
    {
        LdapURL url = new LdapURL();
        url.setHost( getHost() );
        url.setPort( getPort() );
        return url;
    }

}