//--------------------------------------------------
// Interface RepoManager
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package net.moecraft.generator.updater.repo;

@FunctionalInterface
public interface RepoManager {
    Repo[] getRepos() throws Exception;
}
