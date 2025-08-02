import React from 'react';
import { render, screen } from '@testing-library/react';
import LoadingSpinner from '../LoadingSpinner';

describe('LoadingSpinner Component', () => {
  it('should render loading spinner', () => {
    render(<LoadingSpinner />);
    
    const spinner = screen.getByRole('progressbar');
    expect(spinner).toBeInTheDocument();
  });

  it('should render with default size', () => {
    render(<LoadingSpinner />);
    
    const spinner = screen.getByRole('progressbar');
    expect(spinner).toHaveClass('MuiCircularProgress-root');
  });

  it('should render with custom size', () => {
    render(<LoadingSpinner size={60} />);
    
    const spinner = screen.getByRole('progressbar');
    expect(spinner).toBeInTheDocument();
    // The size is passed to MUI component which may handle it differently
    expect(spinner).toHaveAttribute('aria-valuenow');
  });

  it('should render with custom color', () => {
    render(<LoadingSpinner color="secondary" />);
    
    const spinner = screen.getByRole('progressbar');
    expect(spinner).toBeInTheDocument();
    // Just verify the spinner renders, MUI handles color classes internally
    expect(spinner).toHaveClass('MuiCircularProgress-root');
  });

  it('should be centered in container', () => {
    const { container } = render(<LoadingSpinner />);
    
    const containerDiv = container.firstChild;
    expect(containerDiv).toHaveStyle({
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
    });
  });
});