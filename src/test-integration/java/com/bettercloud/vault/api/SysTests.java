package com.bettercloud.vault.api;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.json.JsonObject;
import com.bettercloud.vault.response.SysResponse;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Integration tests for the sys Vault API operations.
 */
public class SysTests {

    private static final String LOCAL_SERVER_ADDRESS = "http://127.0.0.1";
    private static final String DEV_SERVER_ROOT_TOKEN = "813d2212-1054-152e-57d1-cf6ff46f4e81";

    private static final VaultConfig testVaultConfig(int port) {
        try {
            return new VaultConfig()
                    .address(LOCAL_SERVER_ADDRESS + ':' + port)
                    .token(DEV_SERVER_ROOT_TOKEN)
                    .build();
        } catch (VaultException vEx) {
            throw new RuntimeException(vEx);
        }
    }

    public static final Vault vault = new Vault(testVaultConfig(8200))
            .withRetries(10, 100);

    /**
     * Verify we can get the root policy, which always exists.
     *
     * @throws VaultException
     */
    @Test
    public void testGetRootPolicy() throws VaultException {
        final String policy = "root";

        final String policyName = vault.sys().getPolicy("root").getName();

        assertEquals(policy, policyName);
    }

    /**
     * Verify we can post a new policy.
     * <p>
     * Policies are written into configuration files matching the following form:
     * <p>
     * path "secret/foo" {
     * capabilities = ["read"]
     * }
     *
     * @throws VaultException
     */
    @Test
    public void testWriteNewPolicy() throws VaultException {
        final String policyName = "foo-policy";
        final JsonObject policy = new JsonObject().add("path",
                new JsonObject().add("secret/foo/*",
                        new JsonObject().add("policy", "sudo")));

        vault.sys().createPolicy(policyName, policy);
        final SysResponse getResponse = vault.sys().getPolicy(policyName);

        assertEquals(policyName, getResponse.getName());
        assertEquals(policy, getResponse.getRules());
    }
}
