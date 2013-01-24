/**
 * Copyright (C) 2006-2013 phloc systems
 * http://www.phloc.com
 * office[at]phloc[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.phloc.scopes.nonweb.singleton.tree;

import java.util.Collection;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.state.EChange;
import com.phloc.commons.tree.withid.DefaultTreeItemWithID;
import com.phloc.commons.tree.withid.unique.DefaultTreeWithGlobalUniqueID;
import com.phloc.commons.tree.withid.unique.ITreeWithGlobalUniqueID;
import com.phloc.scopes.nonweb.singleton.ApplicationSingleton;

/**
 * Represents a global singleton tree with a unique ID. It basically is a
 * {@link ApplicationSingleton} wrapping a {@link DefaultTreeWithGlobalUniqueID}
 * with the same API.
 * 
 * @author philip
 */
public class ApplicationSingletonTreeWithUniqueID <KEYTYPE, VALUETYPE> extends ApplicationSingleton implements ITreeWithGlobalUniqueID <KEYTYPE, VALUETYPE, DefaultTreeItemWithID <KEYTYPE, VALUETYPE>>
{
  protected final DefaultTreeWithGlobalUniqueID <KEYTYPE, VALUETYPE> m_aTree = new DefaultTreeWithGlobalUniqueID <KEYTYPE, VALUETYPE> ();

  public ApplicationSingletonTreeWithUniqueID ()
  {}

  @Nonnull
  public DefaultTreeItemWithID <KEYTYPE, VALUETYPE> getRootItem ()
  {
    return m_aTree.getRootItem ();
  }

  @Nullable
  public DefaultTreeItemWithID <KEYTYPE, VALUETYPE> getChildWithID (@Nullable final DefaultTreeItemWithID <KEYTYPE, VALUETYPE> aCurrent,
                                                                    @Nullable final KEYTYPE aID)
  {
    return m_aTree.getChildWithID (aCurrent, aID);
  }

  public boolean hasChildren (@Nullable final DefaultTreeItemWithID <KEYTYPE, VALUETYPE> aCurrent)
  {
    return m_aTree.hasChildren (aCurrent);
  }

  @Nonnegative
  public int getChildCount (@Nullable final DefaultTreeItemWithID <KEYTYPE, VALUETYPE> aCurrent)
  {
    return m_aTree.getChildCount (aCurrent);
  }

  @Nullable
  public Collection <? extends DefaultTreeItemWithID <KEYTYPE, VALUETYPE>> getChildren (@Nullable final DefaultTreeItemWithID <KEYTYPE, VALUETYPE> aCurrent)
  {
    return m_aTree.getChildren (aCurrent);
  }

  @Nullable
  public DefaultTreeItemWithID <KEYTYPE, VALUETYPE> getItemWithID (@Nullable final KEYTYPE aDataID)
  {
    return m_aTree.getItemWithID (aDataID);
  }

  @Nonnull
  public Collection <DefaultTreeItemWithID <KEYTYPE, VALUETYPE>> getAllItems ()
  {
    return m_aTree.getAllItems ();
  }

  public boolean isItemSameOrDescendant (@Nullable final KEYTYPE aParentItemID, @Nullable final KEYTYPE aChildItemID)
  {
    return m_aTree.isItemSameOrDescendant (aParentItemID, aChildItemID);
  }

  public boolean containsItemWithID (@Nullable final KEYTYPE aDataID)
  {
    return m_aTree.containsItemWithID (aDataID);
  }

  @Nullable
  public VALUETYPE getItemDataWithID (@Nullable final KEYTYPE aDataID)
  {
    return m_aTree.getItemDataWithID (aDataID);
  }

  @Nonnull
  public Collection <VALUETYPE> getAllItemDatas ()
  {
    return m_aTree.getAllItemDatas ();
  }

  @Nonnull
  public EChange removeItemWithID (@Nullable final KEYTYPE aDataID)
  {
    return m_aTree.removeItemWithID (aDataID);
  }
}
