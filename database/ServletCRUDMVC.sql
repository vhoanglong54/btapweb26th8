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
IF OBJECT_ID(N'dbo.Video', N'U') IS NULL
CREATE TABLE dbo.Video (
    videoId NVARCHAR(50) NOT NULL PRIMARY KEY,
    active BIT NULL CONSTRAINT DF_Video_active DEFAULT 1,
    description NVARCHAR(1000) NULL,
    poster NVARCHAR(255) NULL,
    title NVARCHAR(255) NOT NULL,
    views INT NULL CONSTRAINT DF_Video_views DEFAULT 0,
    categoryId INT NULL,
    CONSTRAINT FK_Video_Category FOREIGN KEY (categoryId) REFERENCES dbo.Category(cate_id)
);
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
    createdDate DATE NOT NULL
);
GO
IF NOT EXISTS (SELECT 1 FROM dbo.[User] WHERE username = N'vuhoanglong')
INSERT dbo.[User](email, username, fullname, password, avatar, roleid, phone, createdDate)
VALUES (N'vuhoanglong@localhost', N'vuhoanglong', N'vuhoanglong', N'vuhoanglong', NULL, 1, N'vuhoanglong', CAST(GETDATE() AS DATE));
GO
