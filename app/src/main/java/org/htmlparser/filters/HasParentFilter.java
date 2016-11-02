// HTMLParser Library - A java-based parser for HTML
// http://htmlparser.org
// Copyright (C) 2006 Derrick Oswald
//
// Revision Control Information
//
// $URL: https://htmlparser.svn.sourceforge.net/svnroot/htmlparser/tags/HTMLParserProject-2.1/parser/src/main/java/org/htmlparser/filters/HasParentFilter.java $
// $Author: derrickoswald $
// $Date: 2006-09-16 16:44:17 +0200 (Sat, 16 Sep 2006) $
// $Revision: 4 $
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the Common Public License; either
// version 1.0 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// Common Public License for more details.
//
// You should have received a copy of the Common Public License
// along with this library; if not, the license is available from
// the Open Source Initiative (OSI) website:
//   http://opensource.org/licenses/cpl1.0.php

package org.htmlparser.filters;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Tag;

/**
 * This class accepts all tags that have a parent acceptable to another filter.
 * It can be set to operate recursively, that is perform a scan up the node
 * heirarchy looking for any ancestor that matches the predicate filter.
 * End tags are not considered to be children of any tag.
 */
public class HasParentFilter
    implements
        NodeFilter
{
    /**
     * The filter to apply to the parent.
     */
    protected NodeFilter mParentFilter;

    /**
     * Performs a recursive search up the node heirarchy if <code>true</code>.
     */
    protected boolean mRecursive;

    /**
     * Creates a new instance of HasParentFilter.
     * With no parent filter, this would always return <code>false</code>
     * from {@link #accept}.
     */
    public HasParentFilter ()
    {
        this (null);
    }

    /**
     * Creates a new instance of HasParentFilter that accepts nodes with
     * the direct parent acceptable to the filter.
     * @param filter The filter to apply to the parent.
     */
    public HasParentFilter (NodeFilter filter)
    {
        this (filter, false);
    }

    /**
     * Creates a new instance of HasParentFilter that accepts nodes with
     * a parent acceptable to the filter.
     * @param filter The filter to apply to the parent.
     * @param recursive If <code>true</code>, any enclosing node acceptable
     * to the given filter causes the node being tested to be accepted
     * (i.e. a recursive scan through the parent nodes up the node
     * heirarchy is performed).
     */
    public HasParentFilter (NodeFilter filter, boolean recursive)
    {
        setParentFilter (filter);
        setRecursive (recursive);
    }

    /**
     * Get the filter used by this HasParentFilter.
     * @return The filter to apply to parents.
     */
    public NodeFilter getParentFilter ()
    {
        return (mParentFilter);
    }

    /**
     * Set the filter for this HasParentFilter.
     * @param filter The filter to apply to parents in {@link #accept}.
     */
    public void setParentFilter (NodeFilter filter)
    {
        mParentFilter = filter;
    }

    /**
     * Get the recusion setting for the filter.
     * @return Returns <code>true</code> if the filter is recursive
     * up the node heirarchy.
     */
    public boolean getRecursive ()
    {
        return (mRecursive);
    }

    /**
     * Sets whether the filter is recursive or not.
     * @param recursive The recursion setting for the filter.
     */
    public void setRecursive (boolean recursive)
    {
        mRecursive = recursive;
    }

    /**
     * Accept tags with parent acceptable to the filter.
     * If recursion is enabled, each parent in turn up to
     * the topmost enclosing node is checked.
     * Recursion only proceeds while no parent satisfies the
     * filter.
     * @param node The node to check.
     * @return <code>true</code> if the node has an acceptable parent,
     * <code>false</code> otherwise.
     */
    public boolean accept (Node node)
    {
        Node parent;
        boolean ret;

        ret = false;
        if (!(node instanceof Tag) || !((Tag)node).isEndTag ())
        {
            parent = node.getParent ();
            if ((null != parent) && (null != getParentFilter ()))
            {
                ret = getParentFilter ().accept (parent);
                if (!ret && getRecursive ())
                    ret = accept (parent);
            }
        }

        return (ret);
    }
}
