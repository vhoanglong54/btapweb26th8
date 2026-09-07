/* SQL Server login required by the exercise. */
USE master;
GO
IF SUSER_ID(N'vuhoanglong') IS NULL
    CREATE LOGIN [vuhoanglong] WITH PASSWORD = N'vuhoanglong', CHECK_POLICY = OFF;
GO
IF DB_ID(N'ServletCRUDMVC') IS NULL CREATE DATABASE ServletCRUDMVC;
GO
USE ServletCRUDMVC;
GO
IF USER_ID(N'vuhoanglong') IS NULL CREATE USER [vuhoanglong] FOR LOGIN [vuhoanglong];
GO
IF NOT EXISTS (
    SELECT 1 FROM sys.database_role_members drm
    JOIN sys.database_principals rolep ON drm.role_principal_id = rolep.principal_id
    JOIN sys.database_principals memberp ON drm.member_principal_id = memberp.principal_id
    WHERE rolep.name = N'db_owner' AND memberp.name = N'vuhoanglong'
)
    ALTER ROLE db_owner ADD MEMBER [vuhoanglong];
GO
IF OBJECT_ID(N'dbo.Category', N'U') IS NULL
CREATE TABLE dbo.Category (
    cate_id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    cate_name NVARCHAR(255) NOT NULL,
    icons NVARCHAR(255) NULL
);
GO
IF COL_LENGTH(N'dbo.Category', N'status') IS NULL
    ALTER TABLE dbo.Category ADD status INT NOT NULL CONSTRAINT DF_Category_status DEFAULT 1;
GO
IF OBJECT_ID(N'dbo.[User]', N'U') IS NULL
CREATE TABLE dbo.[User] (
    id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    email NVARCHAR(255) NOT NULL UNIQUE,
    username NVARCHAR(100) NOT NULL UNIQUE,
    fullname NVARCHAR(255) NOT NULL,
    password NVARCHAR(255) NOT NULL,
    avatar NVARCHAR(255) NULL,
    roleid INT NOT NULL DEFAULT 5,
    phone NVARCHAR(30) NULL UNIQUE,
    createdDate DATE NOT NULL,
    active BIT NOT NULL CONSTRAINT DF_User_active DEFAULT 1
);
GO
IF COL_LENGTH(N'dbo.[User]', N'active') IS NULL
    ALTER TABLE dbo.[User] ADD active BIT NOT NULL CONSTRAINT DF_User_active_legacy DEFAULT 1;
GO
IF COL_LENGTH(N'dbo.[User]', N'enabled') IS NULL
    ALTER TABLE dbo.[User] ADD enabled BIT NOT NULL CONSTRAINT DF_User_enabled DEFAULT 1;
GO
IF OBJECT_ID(N'dbo.UserOtp', N'U') IS NULL
CREATE TABLE dbo.UserOtp (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    email NVARCHAR(255) NOT NULL,
    purpose NVARCHAR(30) NOT NULL,
    codeHash CHAR(64) NOT NULL,
    expiresAt DATETIME2 NOT NULL,
    attempts INT NOT NULL CONSTRAINT DF_UserOtp_attempts DEFAULT 0,
    consumedAt DATETIME2 NULL,
    createdAt DATETIME2 NOT NULL CONSTRAINT DF_UserOtp_created DEFAULT SYSUTCDATETIME()
);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_UserOtp_Validation' AND object_id = OBJECT_ID(N'dbo.UserOtp'))
    CREATE INDEX IX_UserOtp_Validation ON dbo.UserOtp(email, purpose, consumedAt, expiresAt);
GO
IF OBJECT_ID(N'dbo.SmtpSettings', N'U') IS NULL
CREATE TABLE dbo.SmtpSettings (
    id TINYINT NOT NULL CONSTRAINT PK_SmtpSettings PRIMARY KEY CONSTRAINT CK_SmtpSettings_OneRow CHECK (id = 1),
    host NVARCHAR(255) NOT NULL,
    port INT NOT NULL,
    username NVARCHAR(255) NOT NULL,
    password NVARCHAR(500) NOT NULL,
    fromEmail NVARCHAR(255) NOT NULL,
    startTls BIT NOT NULL CONSTRAINT DF_SmtpSettings_StartTls DEFAULT 1,
    updatedAt DATETIME2 NOT NULL CONSTRAINT DF_SmtpSettings_UpdatedAt DEFAULT SYSUTCDATETIME()
);
GO
IF OBJECT_ID(N'dbo.Product', N'U') IS NULL
CREATE TABLE dbo.Product (
    product_id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    product_name NVARCHAR(255) NOT NULL,
    description NVARCHAR(2000) NULL,
    price DECIMAL(18,2) NOT NULL,
    image NVARCHAR(255) NULL,
    status INT NOT NULL CONSTRAINT DF_Product_status DEFAULT 1,
    created_at DATETIME2 NOT NULL CONSTRAINT DF_Product_created_at DEFAULT SYSUTCDATETIME(),
    category_id INT NOT NULL,
    CONSTRAINT FK_Product_Category FOREIGN KEY (category_id) REFERENCES dbo.Category(cate_id)
);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_Product_Newest' AND object_id = OBJECT_ID(N'dbo.Product'))
    CREATE INDEX IX_Product_Newest ON dbo.Product(created_at DESC, product_id DESC);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_Product_Category' AND object_id = OBJECT_ID(N'dbo.Product'))
    CREATE INDEX IX_Product_Category ON dbo.Product(category_id);
GO
IF NOT EXISTS (SELECT 1 FROM dbo.[User] WHERE username = N'vuhoanglong')
INSERT dbo.[User](email, username, fullname, password, avatar, roleid, phone, createdDate)
VALUES (N'vuhoanglong@localhost', N'vuhoanglong', N'vuhoanglong', N'vuhoanglong', NULL, 1, N'vuhoanglong', CAST(GETDATE() AS DATE));
GO
