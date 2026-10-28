package config

import "testing"

func TestBackingIdentifiersFallback(t *testing.T) {
	c := &Config{Project: "My_Proj"}
	if got := c.BackingName(); got != "My_Proj" {
		t.Errorf("BackingName = %q", got)
	}
	if got := c.RootPath(); got != "/Projects/My_Proj/" {
		t.Errorf("RootPath = %q", got)
	}
	if got := c.FeatureStoreName(); got != "my_proj_featurestore" {
		t.Errorf("FeatureStoreName = %q", got)
	}
	if got := c.ModelPath("", "m"); got != "/Projects/My_Proj/Models/m" {
		t.Errorf("ModelPath = %q", got)
	}
}

func TestBackingIdentifiersFromServer(t *testing.T) {
	c := &Config{
		Project:                 "My Proj",
		ProjectBackingName:      "my_proj__abc",
		ProjectHDFSPath:         "/Projects/my_proj__abc",
		OfflineFeaturestoreName: "custom_fs",
	}
	if got := c.BackingName(); got != "my_proj__abc" {
		t.Errorf("BackingName = %q", got)
	}
	if got := c.RootPath(); got != "/Projects/my_proj__abc/" {
		t.Errorf("RootPath = %q", got)
	}
	if got := c.FeatureStoreName(); got != "custom_fs" {
		t.Errorf("FeatureStoreName = %q", got)
	}
	if got := c.ModelPath("/Projects/srv/Models/m/", "m"); got != "/Projects/srv/Models/m" {
		t.Errorf("ModelPath = %q", got)
	}
	if got := c.ModelPath("", "m"); got != "/Projects/my_proj__abc/Models/m" {
		t.Errorf("ModelPath fallback = %q", got)
	}
}

func TestBackingNameFromEnv(t *testing.T) {
	t.Setenv("HOME", t.TempDir())
	t.Setenv("REST_ENDPOINT", "https://h")
	t.Setenv("SECRETS_DIR", t.TempDir())
	t.Setenv("PROJECT_NAME", "Shown")
	t.Setenv("HOPSWORKS_PROJECT_BACKING_NAME", "shown__1")
	c, err := Load()
	if err != nil {
		t.Fatal(err)
	}
	if c.BackingName() != "shown__1" || c.Project != "Shown" {
		t.Errorf("backing=%q project=%q", c.BackingName(), c.Project)
	}

	t.Setenv("HOPSWORKS_PROJECT_BACKING_NAME", "")
	c, _ = Load()
	if c.BackingName() != "Shown" {
		t.Errorf("fallback backing = %q", c.BackingName())
	}
}
