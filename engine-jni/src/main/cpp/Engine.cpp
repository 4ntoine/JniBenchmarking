/*
 * This file is part of Adblock Plus <https://adblockplus.org/>,
 * Copyright (C) 2006-present eyeo GmbH
 *
 * Adblock Plus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation.
 *
 * Adblock Plus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Adblock Plus.  If not, see <http://www.gnu.org/licenses/>.
 */

#include "Engine.h"
#include "Utils.h"


BlockingFilter::BlockingFilter(int pointer): _pointer(pointer)
{
    // nothing
};

BlockingFilter* Engine::matches(const std::string& url,
                                int contentTypeMask,
                                const std::vector<std::string>& documentUrls,
                                const std::string& siteKey,
                                bool specificOnly)
{
    // we consider the exact actions here insignificant,
    // as it's needed for benchmarking only
    return new BlockingFilter(url.size());
}

