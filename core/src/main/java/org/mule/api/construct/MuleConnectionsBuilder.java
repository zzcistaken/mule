/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.api.construct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class MuleConnectionsBuilder
{

    public class MuleConnection
    {
        private String protocol;
        private String address;
        private MuleConnectionDirection direction;
        private boolean connected;
        private String description;

        @Override
        public String toString()
        {
            return protocol + "://" + address + " (" + direction + ", " + connected + ") " + description;
        }

        public String getProtocol()
        {
            return protocol;
        }

        public String getAddress()
        {
            return address;
        }

        public MuleConnectionDirection getDirection()
        {
            return direction;
        }

        public boolean isConnected()
        {
            return connected;
        }

        public String getDescription()
        {
            return description;
        }
    }

    public class MuleConnectionFlow
    {
        private MuleConnection provided;
        private Set<MuleConnection> consumed;

        public MuleConnection getProvidedConnection()
        {
            return provided;
        }

        public Set<MuleConnection> getConsumedConnections()
        {
            return consumed;
        }
    }

    public enum MuleConnectionDirection
    {
        TO, FROM;
    }

    // key is the inbound endpoint (message source) of the flow, values are the outbound endpoints.
    private Map<MuleConnection, Set<MuleConnection>> flowConnections = new HashMap<>();
    private Set<MuleConnection> consumed;

    private Set<MuleConnection> heldBack = new HashSet<>();


    public void setProvided(String protocol, String address, MuleConnectionDirection direction, boolean connected, String description)
    {
        final MuleConnection mc = buildConnection(protocol, address, direction, connected, description);
        consumed = new HashSet<MuleConnection>();
        consumed.addAll(heldBack);
        heldBack.clear();
        flowConnections.put(mc, consumed);
    }

    public void addConsumed(String protocol, String address, MuleConnectionDirection direction, boolean connected, String description)
    {
        final MuleConnection mc = buildConnection(protocol, address, direction, connected, description);
        if (consumed != null)
        {
            consumed.add(mc);
        }
        else
        {
            // hold back
            heldBack.add(mc);
        }
    }

    protected MuleConnection buildConnection(String protocol, String address, MuleConnectionDirection direction, boolean connected, String description)
    {
        final MuleConnection mc = new MuleConnection();
        mc.protocol = protocol.toUpperCase();

        if (address.contains("://"))
        {
            mc.address = address.substring(address.indexOf("://") + 3);
        }
        else
        {
            mc.address = address;
        }

        mc.direction = direction;
        mc.connected = connected;
        mc.description = description;
        return mc;
    }

    @Override
    public String toString()
    {
        return "MuleConnectionsBuilder[flowConnections: " + flowConnections.toString() + "]";
    }

    public List<MuleConnectionFlow> getConnections()
    {
        final List<MuleConnectionFlow> flowConns = new ArrayList<>();

        for (Entry<MuleConnection, Set<MuleConnection>> flowConn : flowConnections.entrySet())
        {
            final MuleConnectionFlow flow = new MuleConnectionFlow();
            flow.provided = flowConn.getKey();
            flow.consumed = flowConn.getValue();
            flowConns.add(flow);
        }

        return flowConns;
    }
}
