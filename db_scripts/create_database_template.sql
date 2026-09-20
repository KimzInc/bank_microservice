-- ============================================================================
-- Template: create a new database for the bank-microservices-starter project
-- ============================================================================
-- HOW TO REUSE THIS FILE FOR A NEW DATABASE:
--   1. Copy this file to create_<service>_db.sql
--   2. Replace every occurrence of REPLACE_DB_NAME with the new database name
--   3. Update the COMMENT ON DATABASE text to describe what the service stores
--   4. If this service connects as a role OTHER than postgres, uncomment the
--      GRANT block below and replace REPLACE_USERNAME too (see PG15+ note)
--   5. Run: sudo -u postgres psql < /path/to/create_<service>_db.sql
-- ============================================================================

-- Uncomment if you need to re-run this script against an existing database
-- (careful: this destroys all data in it)
-- DROP DATABASE IF EXISTS REPLACE_DB_NAME;

-- Create the database
CREATE DATABASE REPLACE_DB_NAME
    WITH
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TEMPLATE = template0;

-- Add a comment describing what this database is for
COMMENT ON DATABASE REPLACE_DB_NAME IS 'Describe what this service stores here.';

-- Grant privileges (only needed if you connect as a role other than postgres;
-- config-repo/*.yml in this project currently connects as the postgres superuser)
   GRANT ALL PRIVILEGES ON DATABASE REPLACE_DB_NAME TO REPLACE_USERNAME;

-- IMPORTANT (PostgreSQL 15+): a database-level GRANT above does NOT include
-- rights inside the "public" schema - since PG15, only the schema owner (and
-- superusers) can CREATE there by default. Without this, Hibernate's
-- ddl-auto: update fails with "permission denied for schema public" the
-- moment it tries to create your first table. \c switches into the new
-- database so the grant below applies to ITS public schema, not postgres's.
   c REPLACE_DB_NAME
   GRANT ALL ON SCHEMA public TO REPLACE_USERNAME;

\echo 'Database REPLACE_DB_NAME created successfully!'
