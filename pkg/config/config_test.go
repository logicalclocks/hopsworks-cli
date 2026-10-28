package config

import (
	"strings"
	"testing"
)

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

func writeSaved(t *testing.T, saved *Config) {
	t.Helper()
	t.Setenv("HOME", t.TempDir())
	if err := saved.Save(); err != nil {
		t.Fatal(err)
	}
}

func TestSavedIdentifiersOnlyForTheSameProject(t *testing.T) {
	saved := &Config{
		Host:                    "https://a",
		APIKey:                  "k",
		Project:                 "Sales",
		ProjectID:               1,
		ProjectBackingName:      "1oldtoken000",
		ProjectHDFSPath:         "/Projects/1oldtoken000/",
		OfflineFeaturestoreName: "1oldtoken000_featurestore",
	}
	cases := []struct {
		name    string
		env     map[string]string
		reused  bool
		backing string
	}{
		{"same identity", map[string]string{}, true, "1oldtoken000"},
		{"other host", map[string]string{"REST_ENDPOINT": "https://b"}, false, "Sales"},
		{"other project id", map[string]string{"HOPSWORKS_PROJECT_ID": "2"}, false, "Sales"},
		{"other backing name", map[string]string{"HOPSWORKS_PROJECT_ID": "1", "HOPSWORKS_PROJECT_BACKING_NAME": "2newtoken000"}, false, "2newtoken000"},
	}
	for _, tc := range cases {
		t.Run(tc.name, func(t *testing.T) {
			writeSaved(t, saved)
			for k, v := range tc.env {
				t.Setenv(k, v)
			}
			c, err := Load()
			if err != nil {
				t.Fatal(err)
			}
			if got := c.ProjectHDFSPath != ""; got != tc.reused {
				t.Errorf("saved path reused = %v, want %v", got, tc.reused)
			}
			if got := c.BackingName(); got != tc.backing {
				t.Errorf("BackingName = %q, want %q", got, tc.backing)
			}
			if !tc.reused && c.FeatureStoreName() != strings.ToLower(tc.backing)+"_featurestore" {
				t.Errorf("FeatureStoreName = %q", c.FeatureStoreName())
			}
		})
	}
}

func TestClearProjectIdentifiers(t *testing.T) {
	c := &Config{Project: "Sales", ProjectBackingName: "x", ProjectHDFSPath: "/Projects/x/", OfflineFeaturestoreName: "x_featurestore"}
	c.ClearProjectIdentifiers()
	if c.ProjectBackingName != "" || c.ProjectHDFSPath != "" || c.OfflineFeaturestoreName != "" {
		t.Errorf("not cleared: %+v", c)
	}
	if c.BackingName() != "Sales" {
		t.Errorf("BackingName = %q", c.BackingName())
	}
}
