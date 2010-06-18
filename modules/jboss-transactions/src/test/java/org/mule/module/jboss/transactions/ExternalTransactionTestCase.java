package org.mule.module.jboss.transactions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;
import org.mule.api.transaction.TransactionCallback;
import org.mule.api.transaction.TransactionConfig;
import org.mule.tck.FunctionalTestCase;
import org.mule.transaction.MuleTransactionConfig;
import org.mule.transaction.TransactionManagerProperties;
import org.mule.transaction.TransactionTemplate;
import org.mule.transaction.XaTransactionFactory;

import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/*
* $Id
* --------------------------------------------------------------------------------------
* Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
*
* The software in this package is published under the terms of the CPAL v1.0
* license, a copy of which has been included with this distribution in the
* LICENSE.txt file.
*/

/** Test transaction behavior when "joinExternal" is set to allow joining external transactions
 * There is one test per legal transactional behavior (e.g. ALWAYS_BEGIN).
 */
public class ExternalTransactionTestCase extends FunctionalTestCase
{
    protected static final Log logger = LogFactory.getLog(ExternalTransactionTestCase.class);
    private MuleContext context;
    private TransactionManagerProperties tmProperties;
    private TransactionManager tm;

    @Override
    protected void doTearDown() throws Exception
    {
        try
        {
            if (tm != null && tm.getTransaction() != null)
                tm.rollback();
        }
        catch (Exception ex)
        {
            logger.debug(ex);
        }
    }

    @Override
    protected String getConfigResources()
    {
        return "org/mule/test/config/external-transaction-config.xml";
    }

    public void testBeginOrJoinTransaction() throws Exception
    {
        init();
        TransactionTemplate tt = createTransactionTemplate(TransactionConfig.ACTION_BEGIN_OR_JOIN);

        tm.begin();
        final Transaction tx = tm.getTransaction();
        final TestResource resource1 = new TestResource(tm);
        tx.enlistResource(resource1);
        assertNotNull(tx);
        String result = (String) tt.execute(new TransactionCallback()
        {
            public Object doInTransaction() throws Exception
            {
                Transaction muleTx = tm.getTransaction();
                assertSame(tx, muleTx);
                resource1.setValue(14);
                return "OK";
            }
        });

        // Not committed yet, since Mule joined the external transaction
        assertEquals("OK", result);
        assertEquals(14, resource1.getValue());
        assertEquals(0, resource1.getPersistentValue());
        tm.commit();

        // Now it's committed
        assertEquals(14, resource1.getPersistentValue());

        // now try with no active transaction
        result = (String) tt.execute(new TransactionCallback()
        {
            public Object doInTransaction() throws Exception
            {
                Transaction muleTx = tm.getTransaction();
                muleTx.enlistResource(resource1);
                resource1.setValue(15);
                return "OK";
            }
        });

        // Mule began and committed the transaction
        assertEquals(15, resource1.getPersistentValue());
    }

    public void testBeginTransaction() throws Exception
    {
        init();
        TransactionTemplate tt = createTransactionTemplate(TransactionConfig.ACTION_ALWAYS_BEGIN);

        tm.begin();
        final Transaction tx = tm.getTransaction();
        final TestResource resource1 = new TestResource(tm);

        assertNotNull(tx);
        String result = (String) tt.execute(new TransactionCallback()
        {
            public Object doInTransaction() throws Exception
            {
                Transaction muleTx = tm.getTransaction();
                assertNotSame(tx, muleTx);
                muleTx.enlistResource(resource1);
                resource1.setValue(14);
                return "OK";
            }
        });

        // Committed in Mule's transaction
        assertEquals("OK", result);
        assertEquals(14, resource1.getValue());
        assertEquals(14, resource1.getPersistentValue());
        tm.commit();

        // Now it's committed
        assertEquals(14, resource1.getPersistentValue());

        result = (String) tt.execute(new TransactionCallback()
        {
            public Object doInTransaction() throws Exception
            {
                Transaction muleTx = tm.getTransaction();
                assertNotSame(tx, muleTx);
                muleTx.enlistResource(resource1);
                resource1.setValue(15);
                return "OK";
            }
        });

        // Committed in Mule's transaction
        assertEquals("OK", result);
        assertEquals(15, resource1.getPersistentValue());
    }

    public void testNoTransactionProcessing() throws Exception
    {
        init();
        TransactionTemplate tt = createTransactionTemplate(TransactionConfig.ACTION_NONE);

        tm.begin();
        final Transaction tx = tm.getTransaction();
        final TestResource resource1 = new TestResource(tm);

        assertNotNull(tx);
        tx.enlistResource(resource1);
        resource1.setValue(14);
        String result = (String) tt.execute(new TransactionCallback()
        {
            public Object doInTransaction() throws Exception
            {
                Transaction muleTx = tm.getTransaction();
                assertNull(muleTx);
                return "OK";
            }
        });

        // transaction restored, no commit
        assertEquals("OK", result);
        assertEquals(14, resource1.getValue());
        assertEquals(0, resource1.getPersistentValue());
        tm.commit();

        // Now it's committed
        assertEquals(14, resource1.getPersistentValue());

        result = (String) tt.execute(new TransactionCallback()
        {
            public Object doInTransaction() throws Exception
            {
                Transaction muleTx = tm.getTransaction();
                assertNull(muleTx);
                return "OK";
            }
        });
    }

    public void testAlwaysJoinTransaction() throws Exception
    {
        init();
        TransactionTemplate tt = createTransactionTemplate(TransactionConfig.ACTION_ALWAYS_JOIN);

        tm.begin();
        final Transaction tx = tm.getTransaction();
        final TestResource resource1 = new TestResource(tm);
        tx.enlistResource(resource1);
        assertNotNull(tx);
        String result = (String) tt.execute(new TransactionCallback()
        {
            public Object doInTransaction() throws Exception
            {
                Transaction muleTx = tm.getTransaction();
                assertSame(tx, muleTx);
                resource1.setValue(14);
                return "OK";
            }
        });

        // Not committed yet, since Mule joined the external transaction
        assertEquals("OK", result);
        assertEquals(14, resource1.getValue());
        assertEquals(0, resource1.getPersistentValue());
        tm.commit();

        // Now it's committed
        assertEquals(14, resource1.getPersistentValue());

        // try with no active transaction.. Should throw
        Exception ex = null;
        try
        {
            result = (String) tt.execute(new TransactionCallback()
            {
                public Object doInTransaction() throws Exception
                {
                    return "OK";
                }
            });
        }
        catch (Exception e)
        {
            ex = e;
            logger.debug("saw exception " + e.getMessage());
        }
        assertNotNull(ex);
    }

    public void testJoinTransactionIfPossible() throws Exception
    {
        init();
        TransactionTemplate tt = createTransactionTemplate(TransactionConfig.ACTION_JOIN_IF_POSSIBLE);

        tm.begin();
        final Transaction tx = tm.getTransaction();
        final TestResource resource1 = new TestResource(tm);
        tx.enlistResource(resource1);
        assertNotNull(tx);
        String result = (String) tt.execute(new TransactionCallback()
        {
            public Object doInTransaction() throws Exception
            {
                Transaction muleTx = tm.getTransaction();
                assertSame(tx, muleTx);
                resource1.setValue(14);
                return "OK";
            }
        });

        // Not committed yet, since Mule joined the external transaction
        assertEquals("OK", result);
        assertEquals(14, resource1.getValue());
        assertEquals(0, resource1.getPersistentValue());
        tm.commit();

        // Now it's committed
        assertEquals(14, resource1.getPersistentValue());

        // try with no active transaction.. Should run with none
        result = (String) tt.execute(new TransactionCallback()
        {
            public Object doInTransaction() throws Exception
            {
                Transaction muleTx = tm.getTransaction();
                assertNull(muleTx);
                return "OK";
            }
        });
        assertEquals("OK", result);
    }

    public void testNoTransactionAllowed() throws Exception
    {
        init();
        TransactionTemplate tt = createTransactionTemplate(TransactionConfig.ACTION_NEVER);

        tm.begin();
        final Transaction tx = tm.getTransaction();
        final TestResource resource1 = new TestResource(tm);
        tx.enlistResource(resource1);
        assertNotNull(tx);

        // This will throw since no transaction is allowed
        Exception ex = null;
        try
        {
            String result = (String) tt.execute(new TransactionCallback()
            {
                public Object doInTransaction() throws Exception
                {
                    return "OK";
                }
            });
        }
        catch (Exception e)
        {
            ex = e;
            logger.debug("saw exception " + e.getMessage());
        }
        assertNotNull(ex);
        tm.rollback();
    }

    private void init() throws Exception
    {
        context = createMuleContext();
        tmProperties = context.getTransactionManagerProperties();
        assertTrue(tmProperties.isJoinExternal());
        tm = context.getTransactionManager();
    }

    private TransactionTemplate createTransactionTemplate(byte action)
    {
        TransactionConfig tc = new MuleTransactionConfig();
        tc.setAction(action);
        tc.setFactory(new XaTransactionFactory());
        TransactionTemplate tt = new TransactionTemplate(tc, null, context);
        return tt;
    }

    /** An XA resource that allows settign, comitting, and rolling back the value of one resource */
    public static class TestResource implements XAResource
    {
        private Map<Transaction, Integer> transientValue = new HashMap<Transaction, Integer>();
        private int persistentValue;
        private TransactionManager tm;

        public TestResource(TransactionManager tm)
        {
            this.tm = tm;
        }

        public void setValue(int val)
        {
            Transaction tx = getCurrentTransaction();
            transientValue.put(tx, val);
        }

        private Transaction getCurrentTransaction()
        {
            Transaction tx = null;
            Exception ex = null;
            try
            {
                tx = tm.getTransaction();
            }
            catch (SystemException e)
            {
                tx = null;
                ex = e;
            }
            if (tx == null)
                throw new IllegalStateException("Unable to access resource value outside transaction", ex);
            return tx;
        }

        public int getPersistentValue()
        {
            return persistentValue;
        }

        public int getValue()
        {
            Transaction tx = null;
            try
            {
                tx = getCurrentTransaction();
            }
            catch (Exception ex)
            {
                ; // return persistent value
            }
            Integer val = transientValue.get(tx);
            return val == null ? persistentValue : val;
        }

        public void commit(Xid id, boolean onePhase) throws XAException
        {
            logger.debug("XA_COMMIT[" + id + "]");
            dumpStackTrace();
            Transaction tx = getCurrentTransaction();
            persistentValue = transientValue.get(tx);
        }

        public void end(Xid xid, int flags) throws XAException
        {
            logger.debug("XA_END[" + xid + "] Flags=" + flags);
            dumpStackTrace();
        }

        public void forget(Xid xid) throws XAException
        {
            logger.debug("XA_FORGET[" + xid + "]");
            dumpStackTrace();
        }

        public int getTransactionTimeout() throws XAException
        {
            return (_timeout);
        }

        public boolean isSameRM(XAResource xares) throws XAException
        {
            return (xares.equals(this));
        }

        public int prepare(Xid xid) throws XAException
        {
            logger.debug("XA_PREPARE[" + xid + "]");
            dumpStackTrace();

            return (XA_OK);
        }

        public Xid[] recover(int flag) throws XAException
        {
            logger.debug("RECOVER[" + flag + "]");
            dumpStackTrace();
            return (null);
        }

        public void rollback(Xid xid) throws XAException
        {
            logger.debug("XA_ROLLBACK[" + xid + "]");
            dumpStackTrace();

            Transaction tx = getCurrentTransaction();
            transientValue.remove(tx);
        }

        public boolean setTransactionTimeout(int seconds) throws XAException
        {
            _timeout = seconds;
            return (true);
        }

        public void start(Xid xid, int flags) throws XAException
        {
            logger.debug("XA_START[" + xid + "] Flags=" + flags);
            dumpStackTrace();
        }

        protected int _timeout = 0;

        private void dumpStackTrace()
        {
            if (logger.isDebugEnabled())
            {
                final StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                new Exception().printStackTrace(pw);
                pw.flush();
                logger.debug(sw.toString());
            }
        }
    }
}