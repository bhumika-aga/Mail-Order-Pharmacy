import { IconButton, Tooltip } from '@mui/material';
import { Brightness4Outlined, Brightness7Outlined } from '@mui/icons-material';
import React from 'react';
import { useTheme } from '../../contexts/ThemeContext';

const ThemeToggle: React.FC = () => {
  const { mode, toggleTheme } = useTheme();

  return (
    <Tooltip title={`Switch to ${mode === 'light' ? 'dark' : 'light'} mode`}>
      <IconButton
        onClick={toggleTheme}
        color="inherit"
      >
        {mode === 'light' ? <Brightness4Outlined /> : <Brightness7Outlined />}
      </IconButton>
    </Tooltip>
  );
};

export default ThemeToggle;